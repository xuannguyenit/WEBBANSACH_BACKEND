package com.xuannguyen.identity.dto.request.cartitem;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCartItemRequest {
    private String cartId;
    private String productId;
    private Integer quantity;
}
