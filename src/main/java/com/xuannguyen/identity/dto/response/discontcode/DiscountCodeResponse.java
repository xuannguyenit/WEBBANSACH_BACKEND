package com.xuannguyen.identity.dto.response.discontcode;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountCodeResponse {

    String id;

    String code;

    Long discountPercentage;

    LocalDate createDate;
}