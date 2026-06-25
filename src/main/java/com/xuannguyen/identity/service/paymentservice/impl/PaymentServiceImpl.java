package com.xuannguyen.identity.service.paymentservice.impl;

import com.xuannguyen.event.dto.NotificationEvent;
import com.xuannguyen.identity.configuration.VnPay;
import com.xuannguyen.identity.dto.response.payment.PayResponse;
import com.xuannguyen.identity.dto.response.payment.PaymentResponse;
import com.xuannguyen.identity.entity.Order;
import com.xuannguyen.identity.entity.PaymentTransaction;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.OrderRepository;
import com.xuannguyen.identity.repository.PaymentTransactionRepository;
import com.xuannguyen.identity.service.orderservice.OrderService;
import com.xuannguyen.identity.service.paymentservice.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final OrderRepository orderRepository;

    private final PaymentTransactionRepository paymentTransactionRepository;

    private final OrderService orderService;

    private static final String PROVIDER_VNPAY = "VNPAY";

    private static final String PAYMENT_PENDING = "PENDING";

    private static final String PAYMENT_SUCCESS = "SUCCESS";

    private static final String PAYMENT_FAILED = "FAILED";

    private static final String ORDER_STATUS_COMPLETE = "Complete";

    private static final String TOPIC_NOTIFICATION_PAYMENT = "notification-payment";

    @Override
    @Transactional
    public PaymentResponse createVnPayPayment(
            String orderId,
            String ipAddress
    ) {
        if (isBlank(orderId)) {
            throw new AppException(ErrorCode.ORDER_ID_REQUIRED);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (ORDER_STATUS_COMPLETE.equalsIgnoreCase(order.getStatus())) {
            throw new AppException(ErrorCode.ORDER_ALREADY_COMPLETED);
        }

        BigDecimal totalPrice = order.getTotalPrice();

        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AppException(ErrorCode.PAYMENT_AMOUNT_INVALID);
        }

        Long vnpAmount = totalPrice
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        String txnRef = VnPay.getRandomNumber(8);

        Map<String, String> vnpParams = buildVnPayParams(
                order,
                txnRef,
                vnpAmount,
                ipAddress
        );

        String paymentUrl = VnPay.buildPaymentUrl(vnpParams);

        PaymentTransaction paymentTransaction = PaymentTransaction.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .txnRef(txnRef)
                .amount(totalPrice)
                .vnpAmount(vnpAmount)
                .provider(PROVIDER_VNPAY)
                .status(PAYMENT_PENDING)
                .orderInfo(order.getId())
                .paymentUrl(paymentUrl)
                .createdAt(LocalDateTime.now())
                .build();

        paymentTransactionRepository.save(paymentTransaction);

        return PaymentResponse.builder()
                .status("OK")
                .message("Create payment url successfully")
                .url(paymentUrl)
                .txnRef(txnRef)
                .orderId(order.getId())
                .build();
    }

    @Override
    @Transactional
    public PayResponse handleVnPayReturn(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            throw new AppException(ErrorCode.PAYMENT_ORDER_INVALID);
        }

        boolean validSignature = VnPay.verifySignature(params);

        if (!validSignature) {
            throw new AppException(ErrorCode.PAYMENT_SIGNATURE_INVALID);
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");
        String transactionNo = params.get("vnp_TransactionNo");
        String bankCode = params.get("vnp_BankCode");
        String cardType = params.get("vnp_CardType");
        String payDate = params.get("vnp_PayDate");
        String vnpAmount = params.get("vnp_Amount");

        if (isBlank(txnRef)) {
            throw new AppException(ErrorCode.PAYMENT_ORDER_INVALID);
        }

        PaymentTransaction paymentTransaction = paymentTransactionRepository
                .findByTxnRef(txnRef)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_EXISTED));

        validateReturnAmount(paymentTransaction, vnpAmount);

        Order order = orderRepository.findById(paymentTransaction.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if (PAYMENT_SUCCESS.equalsIgnoreCase(paymentTransaction.getStatus())) {
            return PayResponse.builder()
                    .orderId(paymentTransaction.getOrderId())
                    .txnRef(txnRef)
                    .transactionNo(paymentTransaction.getVnpTransactionNo())
                    .responseCode(paymentTransaction.getVnpResponseCode())
                    .status(PAYMENT_SUCCESS)
                    .message("Giao dịch đã được xử lý thành công trước đó.")
                    .build();
        }

        boolean paymentSuccess = "00".equals(responseCode)
                && ("00".equals(transactionStatus) || isBlank(transactionStatus));

        if (paymentSuccess) {
            paymentTransaction.setStatus(PAYMENT_SUCCESS);
            paymentTransaction.setPaidAt(LocalDateTime.now());
            paymentTransaction.setVnpResponseCode(responseCode);
            paymentTransaction.setVnpTransactionNo(transactionNo);
            paymentTransaction.setVnpBankCode(bankCode);
            paymentTransaction.setVnpCardType(cardType);
            paymentTransaction.setVnpPayDate(payDate);

            PaymentTransaction savedPaymentTransaction =
                    paymentTransactionRepository.save(paymentTransaction);

            orderService.updateOrder(savedPaymentTransaction.getOrderId());

            Order updatedOrder = orderRepository.findById(savedPaymentTransaction.getOrderId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

            sendPaymentSuccessNotification(
                    savedPaymentTransaction,
                    updatedOrder,
                    transactionNo,
                    responseCode
            );

            return PayResponse.builder()
                    .orderId(savedPaymentTransaction.getOrderId())
                    .txnRef(txnRef)
                    .transactionNo(transactionNo)
                    .responseCode(responseCode)
                    .status(PAYMENT_SUCCESS)
                    .message("Thanh toán thành công.")
                    .build();
        }

        paymentTransaction.setStatus(PAYMENT_FAILED);
        paymentTransaction.setVnpResponseCode(responseCode);
        paymentTransaction.setVnpTransactionNo(transactionNo);
        paymentTransaction.setVnpBankCode(bankCode);
        paymentTransaction.setVnpCardType(cardType);
        paymentTransaction.setVnpPayDate(payDate);

        PaymentTransaction savedPaymentTransaction =
                paymentTransactionRepository.save(paymentTransaction);

        sendPaymentFailedNotification(
                savedPaymentTransaction,
                order,
                transactionNo,
                responseCode
        );

        return PayResponse.builder()
                .orderId(savedPaymentTransaction.getOrderId())
                .txnRef(txnRef)
                .transactionNo(transactionNo)
                .responseCode(responseCode)
                .status(PAYMENT_FAILED)
                .message("Thanh toán thất bại.")
                .build();
    }

    private Map<String, String> buildVnPayParams(
            Order order,
            String txnRef,
            Long vnpAmount,
            String ipAddress
    ) {
        String clientIp = isBlank(ipAddress) ? "127.0.0.1" : ipAddress;

        Map<String, String> vnpParams = new HashMap<>();

        vnpParams.put("vnp_Version", VnPay.VNP_VERSION);
        vnpParams.put("vnp_Command", VnPay.VNP_COMMAND);
        vnpParams.put("vnp_TmnCode", VnPay.VNP_TMN_CODE);
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_CurrCode", VnPay.VNP_CURR_CODE);
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", order.getId());
        vnpParams.put("vnp_Locale", VnPay.VNP_LOCALE);
        vnpParams.put("vnp_OrderType", VnPay.VNP_ORDER_TYPE);

        /*
         * ReturnUrl nên trỏ về backend để:
         * - verify chữ ký
         * - lưu PaymentTransaction
         * - update Order
         */
        vnpParams.put(
                "vnp_ReturnUrl",
                "http://localhost:8080/identity/payment/vnpay-return"
        );

        vnpParams.put("vnp_IpAddr", clientIp);

        Calendar calendar = Calendar.getInstance();

        Date createDate = calendar.getTime();
        vnpParams.put("vnp_CreateDate", VnPay.formatVnPayDate(createDate));

        calendar.add(Calendar.MINUTE, 15);
        Date expireDate = calendar.getTime();
        vnpParams.put("vnp_ExpireDate", VnPay.formatVnPayDate(expireDate));

        return vnpParams;
    }

    private void validateReturnAmount(
            PaymentTransaction paymentTransaction,
            String vnpAmount
    ) {
        if (paymentTransaction == null) {
            throw new AppException(ErrorCode.PAYMENT_NOT_EXISTED);
        }

        if (isBlank(vnpAmount)) {
            throw new AppException(ErrorCode.PAYMENT_AMOUNT_INVALID);
        }

        Long returnedAmount;

        try {
            returnedAmount = Long.valueOf(vnpAmount);
        } catch (NumberFormatException exception) {
            throw new AppException(ErrorCode.PAYMENT_AMOUNT_INVALID);
        }

        if (!returnedAmount.equals(paymentTransaction.getVnpAmount())) {
            throw new AppException(ErrorCode.PAYMENT_AMOUNT_INVALID);
        }
    }

    private void sendPaymentSuccessNotification(
            PaymentTransaction paymentTransaction,
            Order order,
            String transactionNo,
            String responseCode
    ) {
        if (paymentTransaction == null || order == null || isBlank(order.getEmail())) {
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("orderId", order.getId());
        param.put("userId", order.getUserId());
        param.put("txnRef", paymentTransaction.getTxnRef());
        param.put("transactionNo", transactionNo);
        param.put("responseCode", responseCode);
        param.put("amount", paymentTransaction.getAmount());
        param.put("vnpAmount", paymentTransaction.getVnpAmount());
        param.put("provider", paymentTransaction.getProvider());
        param.put("paymentStatus", PAYMENT_SUCCESS);
        param.put("orderStatus", order.getStatus());
        param.put("bankCode", paymentTransaction.getVnpBankCode());
        param.put("cardType", paymentTransaction.getVnpCardType());
        param.put("payDate", paymentTransaction.getVnpPayDate());
        param.put("paidAt", paymentTransaction.getPaidAt() != null
                ? paymentTransaction.getPaidAt().toString()
                : null);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(order.getEmail())
                .templateCode("PAYMENT_SUCCESS")
                .subject("Thanh toán thành công")
                .body(
                        "Thanh toán đơn hàng thành công."
                                + " Mã đơn hàng: " + order.getId()
                                + " - Mã thanh toán: " + paymentTransaction.getTxnRef()
                                + " - Mã giao dịch VNPAY: " + transactionNo
                                + " - Số tiền: " + paymentTransaction.getAmount()
                                + " - Trạng thái: " + PAYMENT_SUCCESS
                )
                .param(param)
                .build();

        kafkaTemplate.send(TOPIC_NOTIFICATION_PAYMENT, notificationEvent);
    }

    private void sendPaymentFailedNotification(
            PaymentTransaction paymentTransaction,
            Order order,
            String transactionNo,
            String responseCode
    ) {
        if (paymentTransaction == null || order == null || isBlank(order.getEmail())) {
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("orderId", order.getId());
        param.put("userId", order.getUserId());
        param.put("txnRef", paymentTransaction.getTxnRef());
        param.put("transactionNo", transactionNo);
        param.put("responseCode", responseCode);
        param.put("amount", paymentTransaction.getAmount());
        param.put("vnpAmount", paymentTransaction.getVnpAmount());
        param.put("provider", paymentTransaction.getProvider());
        param.put("paymentStatus", PAYMENT_FAILED);
        param.put("orderStatus", order.getStatus());
        param.put("bankCode", paymentTransaction.getVnpBankCode());
        param.put("cardType", paymentTransaction.getVnpCardType());
        param.put("payDate", paymentTransaction.getVnpPayDate());

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(order.getEmail())
                .templateCode("PAYMENT_FAILED")
                .subject("Thanh toán thất bại")
                .body(
                        "Thanh toán đơn hàng thất bại."
                                + " Mã đơn hàng: " + order.getId()
                                + " - Mã thanh toán: " + paymentTransaction.getTxnRef()
                                + " - Số tiền: " + paymentTransaction.getAmount()
                                + " - Mã phản hồi: " + responseCode
                )
                .param(param)
                .build();

        kafkaTemplate.send(TOPIC_NOTIFICATION_PAYMENT, notificationEvent);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
