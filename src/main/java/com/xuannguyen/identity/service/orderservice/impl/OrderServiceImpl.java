package com.xuannguyen.identity.service.orderservice.impl;

import com.xuannguyen.event.dto.NotificationEvent;
import com.xuannguyen.identity.controller.order.RevenueDto;
import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.order.OrderUserRequest;
import com.xuannguyen.identity.dto.request.order.ProductUpdateQuantityRequest;
import com.xuannguyen.identity.dto.response.orderresponse.DailyRevenueResponse;
import com.xuannguyen.identity.entity.*;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.*;
import com.xuannguyen.identity.service.orderservice.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final CartRepository cartRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = authentication.getName();

        if (isBlank(username)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private Product getProductById(String productId) {
        if (isBlank(productId)) {
            throw new AppException(ErrorCode.PRODUCT_ID_REQUIRED);
        }

        return productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
    }

    @Override
    public List<Order> getOrderUserByUserId(Long userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.USER_ID_REQUIRED);
        }

        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getOrderUserByOrderIdComplete() {
        User user = getCurrentUser();

        return orderRepository.getAllByUserIdComplete(user.getId());
    }

    @Override
    public List<OrderDetail> getProductOfUser() {
        User user = getCurrentUser();

        List<Order> orders = orderRepository.getAllByUserIdComplete(user.getId());

        List<OrderDetail> result = new ArrayList<>();

        for (Order order : orders) {
            List<OrderDetail> orderDetails = order.getOrderDetails();

            if (orderDetails == null || orderDetails.isEmpty()) {
                continue;
            }

            for (OrderDetail item : orderDetails) {
                OrderDetail responseItem = new OrderDetail();
                responseItem.setProductId(item.getProductId());
                responseItem.setProductName(item.getProductName());
                responseItem.setProductPrice(item.getProductPrice());
                responseItem.setProductQuantity(item.getProductQuantity());
                responseItem.setTotalPrice(item.getTotalPrice());
                responseItem.setProductSalePrice(item.getProductSalePrice());
                result.add(responseItem);
            }
        }
        return result;
    }

    @Override
    public List<OrderDetail> getAllOderDetail(String orderId) {
        if (isBlank(orderId)) {
            throw new AppException(ErrorCode.ORDER_ID_REQUIRED);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        return orderDetailRepository.findByOrderId(order.getId());
    }

    @Override
    public Order getOrderById(String id) {
        if (isBlank(id)) {
            throw new AppException(ErrorCode.ORDER_ID_REQUIRED);
        }

        return orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
    }

    @Override
    @Transactional
    public Order createOrderFromCart(OrderUserRequest orderRequest) {
        User user = getCurrentUser();

        if (orderRequest == null) {
            throw new AppException(ErrorCode.ORDER_DETAIL_REQUEST_INVALID);
        }

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        validateCartBeforeCreateOrder(cart);

        BigDecimal cartTotalPrice = cart.getTotalPrice() == null
                ? BigDecimal.ZERO
                : cart.getTotalPrice();

        Order order = Order.builder()
                .userId(user.getId())
                .fullName(orderRequest.getFullName())
                .email(orderRequest.getEmail())
                .phone(orderRequest.getPhone())
                .address(orderRequest.getAddress())
                .orderTime(LocalDateTime.now())
                .status("Pending")
                .totalPrice(cartTotalPrice)
                .shippingMethod("VNPAY")
                .paymentMethod("VNPAY")
                .shippingDate(LocalDateTime.now().plusDays(10))
                .build();

        Order savedOrder = orderRepository.save(order);

        List<OrderDetail> orderDetails = cart.getCartItems()
                .stream()
                .map(cartItem -> mapCartItemToOrderDetail(cartItem, savedOrder))
                .toList();

        orderDetailRepository.saveAll(orderDetails);

        sendOrderNotification(savedOrder, orderRequest);

        return savedOrder;
    }

    @Override
    @Transactional
    public Order updateOrder(String id) {
        if (isBlank(id)) {
            throw new AppException(ErrorCode.ORDER_ID_REQUIRED);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if ("Complete".equalsIgnoreCase(order.getStatus())) {
            return order;
        }

        List<OrderDetail> orderDetails = order.getOrderDetails();

        if (orderDetails == null || orderDetails.isEmpty()) {
            throw new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED);
        }

        for (OrderDetail orderDetail : orderDetails) {
            decreaseProductQuantityAfterOrder(orderDetail);
        }

        String transactionReference = getTransactionReferenceFromRequest();

        order.setOrderTime(LocalDateTime.now());
        order.setStatus("Complete");
        order.setTransactionReference(transactionReference);

        Order savedOrder = orderRepository.save(order);

        deleteUserCartIfExists(savedOrder.getUserId());

        return savedOrder;
    }

    @Override
    public List<Order> getAllOrderByUserId() {
        User user = getCurrentUser();

        return orderRepository.getAllByUserId(user.getId());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public RevenueDto getRevenueStatistics() {
        List<Order> orders = orderRepository.getAllOrderCompleted();

        BigDecimal revenue = orders.stream()
                .map(Order::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RevenueDto.builder()
                .revenue(revenue)
                .build();
    }

    @Override
    public BigDecimal getRevenueStatisticsByTime(LocalDate dateStart, LocalDate dateEnd) {
        if (dateStart == null || dateEnd == null) {
            return BigDecimal.ZERO;
        }

        if (dateEnd.isBefore(dateStart)) {
            return BigDecimal.ZERO;
        }

        List<Order> completedOrders = orderRepository.getAllOrderCompleted();

        return completedOrders.stream()
                .filter(order -> order.getOrderTime() != null)
                .filter(order -> {
                    LocalDate orderDate = order.getOrderTime().toLocalDate();
                    return !orderDate.isBefore(dateStart)
                            && !orderDate.isAfter(dateEnd);
                })
                .map(Order::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrderDesc() {
        return orderRepository.getAllOrderDesc();
    }

    @Override
    public List<DailyRevenueResponse> getDailyRevenueForMonth(int month, int year) {
        if (month < 1 || month > 12) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        if (year < 1900) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        List<Object[]> rows = orderRepository.getDailyRevenueForMonthRaw(month, year);

        return rows.stream()
                .map(row -> {
                    Object dateValue = row[0];
                    Object revenueValue = row[1];

                    LocalDate date;

                    if (dateValue instanceof java.sql.Date sqlDate) {
                        date = sqlDate.toLocalDate();
                    } else {
                        date = LocalDate.parse(dateValue.toString());
                    }

                    BigDecimal revenue;

                    if (revenueValue instanceof BigDecimal bigDecimal) {
                        revenue = bigDecimal;
                    } else {
                        revenue = new BigDecimal(revenueValue.toString());
                    }

                    return DailyRevenueResponse.builder()
                            .date(date)
                            .revenue(revenue)
                            .build();
                })
                .toList();
    }

    @Override
    public Long getCountOrder() {
        return orderRepository.getCountOrder();
    }

    @Override
    public List<OrderDetail> getListOrderDetailByOrderStatus() {
        return orderRepository.getPendingOrderDetails();
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteOrder(String id) {
        if (isBlank(id)) {
            throw new AppException(ErrorCode.ORDER_ID_REQUIRED);
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        orderRepository.delete(order);

        return ApiResponse.<Boolean>builder()
                .message("Delete success")
                .result(true)
                .build();
    }

    private void validateCartBeforeCreateOrder(Cart cart) {
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = getProductById(cartItem.getProductId());

            Integer cartQuantity = cartItem.getProductQuantity();

            if (cartQuantity == null || cartQuantity <= 0) {
                throw new AppException(ErrorCode.PRODUCT_QUANTITY_INVALID);
            }

            if (product.getEnable() != null && !product.getEnable()) {
                throw new AppException(ErrorCode.PRODUCT_DISABLED);
            }

            if (product.getQuantity() < cartQuantity) {
                throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY_OF_PRODUCTS);
            }
        }
    }

    private OrderDetail mapCartItemToOrderDetail(CartItem cartItem, Order savedOrder) {
        Product product = getProductById(cartItem.getProductId());

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(savedOrder);
        orderDetail.setProductId(product.getId());
        orderDetail.setProductName(product.getName());
        orderDetail.setProductPrice(cartItem.getProductPrice());
        orderDetail.setProductQuantity(cartItem.getProductQuantity());
        orderDetail.setProductSalePrice(cartItem.getDiscountPercentage());
        orderDetail.setTotalPrice(cartItem.getTotalPrice());

        return orderDetail;
    }

    private void decreaseProductQuantityAfterOrder(OrderDetail orderDetail) {
        if (orderDetail == null) {
            throw new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED);
        }

        Product product = getProductById(orderDetail.getProductId());

        Integer orderQuantity = orderDetail.getProductQuantity();

        if (orderQuantity == null || orderQuantity <= 0) {
            throw new AppException(ErrorCode.PRODUCT_QUANTITY_INVALID);
        }

        if (product.getQuantity() < orderQuantity) {
            throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY_OF_PRODUCTS);
        }

        product.setQuantity(product.getQuantity() - orderQuantity);

        productRepository.save(product);
    }

    private void deleteUserCartIfExists(Long userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.USER_ID_REQUIRED);
        }

        if (cartRepository.existsByUserId(userId)) {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

            cartRepository.delete(cart);
        }
    }

    private String getTransactionReferenceFromRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();

        if (request == null) {
            return null;
        }

        return request.getParameter("vnp_TxnRef");
    }

    private void sendOrderNotification(Order savedOrder, OrderUserRequest orderRequest) {
        if (savedOrder == null || orderRequest == null || isBlank(orderRequest.getEmail())) {
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("orderId", savedOrder.getId());
        param.put("userId", savedOrder.getUserId());
        param.put("fullName", savedOrder.getFullName());
        param.put("email", savedOrder.getEmail());
        param.put("phone", savedOrder.getPhone());
        param.put("address", savedOrder.getAddress());
        param.put("totalPrice", savedOrder.getTotalPrice());
        param.put("status", savedOrder.getStatus());
        param.put("paymentMethod", savedOrder.getPaymentMethod());
        param.put("shippingMethod", savedOrder.getShippingMethod());
        param.put("orderTime", savedOrder.getOrderTime() != null
                ? savedOrder.getOrderTime().toString()
                : null);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(orderRequest.getEmail())
                .templateCode("ORDER_CREATED")
                .subject("Đặt hàng thành công")
                .body(
                        "Đơn hàng của bạn đã được tạo thành công."
                                + " Mã đơn hàng: " + savedOrder.getId()
                                + " - Tên người nhận: " + savedOrder.getFullName()
                                + " - Số điện thoại: " + savedOrder.getPhone()
                                + " - Địa chỉ: " + savedOrder.getAddress()
                                + " - Tổng tiền: " + savedOrder.getTotalPrice()
                                + " - Trạng thái: " + savedOrder.getStatus()
                )
                .param(param)
                .build();

        kafkaTemplate.send("notification-order", notificationEvent);
    }
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}


