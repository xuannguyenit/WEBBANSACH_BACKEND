package com.xuannguyen.identity.dto.request.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdateQuantityRequest {
    private String productId;
    private int quantity;
}
