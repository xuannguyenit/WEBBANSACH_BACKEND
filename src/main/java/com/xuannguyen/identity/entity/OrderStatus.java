package com.xuannguyen.identity.entity;

public enum OrderStatus {
    PENDING("PENDING"),         // Chờ xử lý
    PROCESSING("PROCESSING"),   // Đang xử lý
    SHIPPED("SHIPPED"),         // Đã gửi hàng
    DELIVERED("DELIVERED"),     // Đã giao hàng
    COMPLETED("COMPLETED"),     // Hoàn thành
    CANCELLED("CANCELLED"),     // Đã hủy
    RETURNED("RETURNED"),       // Đã trả lại
    REFUNDED("REFUNDED"),       // Đã hoàn tiền
    ON_HOLD("ON_HOLD");         // Đang giữ
    ;

    OrderStatus(String message) {

        this.message = message;
    }
    private String message;
}
