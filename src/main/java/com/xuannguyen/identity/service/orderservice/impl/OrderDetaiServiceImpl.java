package com.xuannguyen.identity.service.orderservice.impl;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.order.OrderDetailRequest;
import com.xuannguyen.identity.entity.DiscountCode;
import com.xuannguyen.identity.entity.Order;
import com.xuannguyen.identity.entity.OrderDetail;
import com.xuannguyen.identity.entity.Product;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.OrderDetailRepository;
import com.xuannguyen.identity.repository.OrderRepository;
import com.xuannguyen.identity.repository.ProductRepository;
import com.xuannguyen.identity.service.orderservice.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class OrderDetaiServiceImpl implements OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailRequest request) {
        validateOrderDetailRequest(request);

        Order order = findOrderById(request.getOrderid());

        Product product = findProductById(request.getProductId());

        validateProductStock(product, request.getProductQuantity());

        Long discountPercentage = getDiscountPercentage(product);

        BigDecimal totalPrice = calculateTotalPrice(
                product.getPrice(),
                request.getProductQuantity(),
                discountPercentage
        );

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProductId(product.getId());
        orderDetail.setProductName(product.getName());
        orderDetail.setProductPrice(product.getPrice());
        orderDetail.setProductSalePrice(discountPercentage);
        orderDetail.setProductQuantity(request.getProductQuantity());
        orderDetail.setTotalPrice(totalPrice);

        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        updateOrderTotalPrice(order);

        return savedOrderDetail;
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(String id, OrderDetailRequest request) {
        if (isBlank(id)) {
            throw new AppException(ErrorCode.ORDER_DETAIL_ID_REQUIRED);
        }

        validateOrderDetailRequest(request);

        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));

        Order order = findOrderById(request.getOrderid());

        Product product = findProductById(request.getProductId());

        validateProductStock(product, request.getProductQuantity());

        Long discountPercentage = getDiscountPercentage(product);

        BigDecimal totalPrice = calculateTotalPrice(
                product.getPrice(),
                request.getProductQuantity(),
                discountPercentage
        );

        orderDetail.setOrder(order);
        orderDetail.setProductId(product.getId());
        orderDetail.setProductName(product.getName());
        orderDetail.setProductPrice(product.getPrice());
        orderDetail.setProductSalePrice(discountPercentage);
        orderDetail.setProductQuantity(request.getProductQuantity());
        orderDetail.setTotalPrice(totalPrice);

        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        updateOrderTotalPrice(order);

        return savedOrderDetail;
    }

    @Override
    @Transactional
    public void deleteOrderDetail(String id) {
        if (isBlank(id)) {
            throw new AppException(ErrorCode.ORDER_DETAIL_ID_REQUIRED);
        }

        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));

        Order order = orderDetail.getOrder();

        orderDetailRepository.delete(orderDetail);

        if (order != null) {
            updateOrderTotalPrice(order);
        }
    }

    @Override
    public List<OrderDetail> getAllOrderDetail() {
        return orderDetailRepository.findAll();
    }

    @Override
    public List<OrderDetail> getOrderDetailByOrderId(String id) {
        if (isBlank(id)) {
            throw new AppException(ErrorCode.ORDER_ID_REQUIRED);
        }

        Order order = findOrderById(id);

        return orderDetailRepository.findByOrderId(order.getId());
    }

    @Override
    public OrderDetail getOrderDetailById(String id) {
        if (isBlank(id)) {
            throw new AppException(ErrorCode.ORDER_DETAIL_ID_REQUIRED);
        }

        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));
    }



    @Override
    @Transactional
    public OrderDetail addToCart(Long userId, OrderDetailRequest request) {


        if (request == null) {
            throw new AppException(ErrorCode.ORDER_DETAIL_REQUEST_INVALID);
        }

        if (isBlank(request.getProductId())) {
            throw new AppException(ErrorCode.PRODUCT_ID_REQUIRED);
        }

        if (request.getProductQuantity() == null || request.getProductQuantity() <= 0) {
            throw new AppException(ErrorCode.PRODUCT_QUANTITY_INVALID);
        }

        Order order = orderRepository.findByUserIdAndStatus(userId, "PENDING")
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setUserId(userId);
                    newOrder.setStatus("PENDING");
                    newOrder.setTotalPrice(BigDecimal.ZERO);
                    return orderRepository.save(newOrder);
                });

        Product product = findProductById(request.getProductId());

        validateProductStock(product, request.getProductQuantity());

        Long discountPercentage = getDiscountPercentage(product);

        List<OrderDetail> orderDetails =
                orderDetailRepository.findAllByOrderIdAndProductId(
                        order.getId(),
                        product.getId()
                );

        OrderDetail existingOrderDetail = orderDetails.isEmpty()
                ? null
                : orderDetails.get(0);

        if (existingOrderDetail != null) {
            int newQuantity = existingOrderDetail.getProductQuantity()
                    + request.getProductQuantity();

            if (newQuantity > product.getQuantity()) {
                throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY_OF_PRODUCTS);
            }

            existingOrderDetail.setProductName(product.getName());
            existingOrderDetail.setProductPrice(product.getPrice());
            existingOrderDetail.setProductSalePrice(discountPercentage);
            existingOrderDetail.setProductQuantity(newQuantity);
            existingOrderDetail.setTotalPrice(
                    calculateTotalPrice(
                            product.getPrice(),
                            newQuantity,
                            discountPercentage
                    )
            );

            OrderDetail savedOrderDetail = orderDetailRepository.save(existingOrderDetail);

            updateOrderTotalPrice(order);

            return savedOrderDetail;
        }

        OrderDetail newOrderDetail = new OrderDetail();
        newOrderDetail.setOrder(order);
        newOrderDetail.setProductId(product.getId());
        newOrderDetail.setProductName(product.getName());
        newOrderDetail.setProductPrice(product.getPrice());
        newOrderDetail.setProductSalePrice(discountPercentage);
        newOrderDetail.setProductQuantity(request.getProductQuantity());
        newOrderDetail.setTotalPrice(
                calculateTotalPrice(
                        product.getPrice(),
                        request.getProductQuantity(),
                        discountPercentage
                )
        );

        OrderDetail savedOrderDetail = orderDetailRepository.save(newOrderDetail);

        updateOrderTotalPrice(order);

        return savedOrderDetail;
    }

    private void validateOrderDetailRequest(OrderDetailRequest request) {
        if (request == null) {
            throw new AppException(ErrorCode.ORDER_DETAIL_REQUEST_INVALID);
        }

        if (isBlank(request.getOrderid())) {
            throw new AppException(ErrorCode.ORDER_ID_REQUIRED);
        }

        if (isBlank(request.getProductId())) {
            throw new AppException(ErrorCode.PRODUCT_ID_REQUIRED);
        }

        if (request.getProductQuantity() == null || request.getProductQuantity() <= 0) {
            throw new AppException(ErrorCode.PRODUCT_QUANTITY_INVALID);
        }
    }

    private Order findOrderById(String orderId) {
        if (isBlank(orderId)) {
            throw new AppException(ErrorCode.ORDER_ID_REQUIRED);
        }

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
    }

    private Product findProductById(String productId) {
        if (isBlank(productId)) {
            throw new AppException(ErrorCode.PRODUCT_ID_REQUIRED);
        }

        return productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
    }

    private void validateProductStock(Product product, Integer requestedQuantity) {
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }

        if (requestedQuantity == null || requestedQuantity <= 0) {
            throw new AppException(ErrorCode.PRODUCT_QUANTITY_INVALID);
        }

        if (product.getQuantity() <= 0) {
            throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY_OF_PRODUCTS);
        }

        if (requestedQuantity > product.getQuantity()) {
            throw new AppException(ErrorCode.INSUFFICIENT_QUANTITY_OF_PRODUCTS);
        }
    }

    private Long getDiscountPercentage(Product product) {
        if (product == null || product.getDiscountCode() == null) {
            return 0L;
        }

        DiscountCode discountCode = product.getDiscountCode();

        Long discountPercentage = discountCode.getDiscountPercentage();

        if (discountPercentage == null || discountPercentage < 0) {
            return 0L;
        }

        if (discountPercentage > 100) {
            return 100L;
        }

        return discountPercentage;
    }

    private BigDecimal calculateTotalPrice(
            BigDecimal productPrice,
            Integer quantity,
            Long discountPercentage
    ) {
        if (productPrice == null) {
            productPrice = BigDecimal.ZERO;
        }

        if (quantity == null || quantity <= 0) {
            throw new AppException(ErrorCode.PRODUCT_QUANTITY_INVALID);
        }

        BigDecimal priceAfterDiscount = calculatePriceAfterDiscount(
                productPrice,
                discountPercentage
        );

        return priceAfterDiscount
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePriceAfterDiscount(
            BigDecimal price,
            Long discountPercentage
    ) {
        if (price == null) {
            return BigDecimal.ZERO;
        }

        if (discountPercentage == null || discountPercentage <= 0) {
            return price;
        }

        BigDecimal discountRate = BigDecimal.valueOf(discountPercentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return price.subtract(price.multiply(discountRate))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void updateOrderTotalPrice(Order order) {
        if (order == null || isBlank(order.getId())) {
            throw new AppException(ErrorCode.ORDER_NOT_EXISTED);
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());

        BigDecimal newTotalPrice = orderDetails.stream()
                .map(OrderDetail::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(newTotalPrice);

        orderRepository.save(order);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
