package com.xuannguyen.identity.dto.response.cartitem;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {
    private String id;
    private String cartId;
    private String productId;
    private String productName;
    private BigDecimal productPrice;
    private int productQuantity;
    private long discountPercentage;
    private BigDecimal totalPrice;
    private Long userId;
}
