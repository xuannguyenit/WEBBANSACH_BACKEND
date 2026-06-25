package com.xuannguyen.identity.dto.response.productbook;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductBookImportError {

    int rowIndex;

    String name;

    String message;
}