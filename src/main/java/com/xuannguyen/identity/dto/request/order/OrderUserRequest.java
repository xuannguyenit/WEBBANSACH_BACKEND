package com.xuannguyen.identity.dto.request.order;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderUserRequest {
    private String fullName;

    private String email;
    @NotEmpty(message = "số điện thoại không được trống")
    private String phone;
    @NotEmpty(message = "Địa chỉ người nhận không được trống")
    private String address;

    private String shippingMethod;
    private String shippingAddress;
    private String trackingNumber;
    private String paymentMethod;
}
