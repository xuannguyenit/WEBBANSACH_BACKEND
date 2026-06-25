package com.xuannguyen.identity.dto.request.discontcode;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountCodeRequest {

    String code;

    Long discountPercentage;
}