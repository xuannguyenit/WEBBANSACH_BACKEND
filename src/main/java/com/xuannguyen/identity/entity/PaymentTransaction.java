package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_transaction")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Mã đơn hàng trong hệ thống.
     */
    @Column(name = "order_id", nullable = false)
    private String orderId;

    /**
     * User đặt hàng.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Mã giao dịch do mình sinh gửi sang VNPAY.
     */
    @Column(name = "txn_ref", nullable = false, unique = true)
    private String txnRef;

    /**
     * Tổng tiền gốc của đơn hàng.
     */
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Số tiền gửi sang VNPAY, theo quy ước nhân 100.
     */
    @Column(name = "vnp_amount")
    private Long vnpAmount;

    /**
     * Nhà cung cấp thanh toán.
     */
    @Column(name = "provider")
    private String provider;

    /**
     * Trạng thái nội bộ: PENDING, SUCCESS, FAILED.
     */
    @Column(name = "status")
    private String status;

    /**
     * Mã phản hồi từ VNPAY.
     */
    @Column(name = "vnp_response_code")
    private String vnpResponseCode;

    /**
     * Mã giao dịch phía VNPAY.
     */
    @Column(name = "vnp_transaction_no")
    private String vnpTransactionNo;

    /**
     * Mã ngân hàng.
     */
    @Column(name = "vnp_bank_code")
    private String vnpBankCode;

    /**
     * Loại thẻ / phương thức thanh toán.
     */
    @Column(name = "vnp_card_type")
    private String vnpCardType;

    /**
     * Thời gian thanh toán từ VNPAY.
     */
    @Column(name = "vnp_pay_date")
    private String vnpPayDate;

    /**
     * Nội dung thanh toán.
     */
    @Column(name = "order_info")
    private String orderInfo;

    /**
     * URL thanh toán đã tạo.
     */
    @Column(name = "payment_url", columnDefinition = "TEXT")
    private String paymentUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
