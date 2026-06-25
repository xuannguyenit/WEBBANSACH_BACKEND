package com.xuannguyen.identity.dto.response.productbook;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductBookImportResponse {

    int totalRows;

    int successCount;

    int failedCount;

    @Builder.Default
    List<ProductBookImportError> errors = new ArrayList<>();
}
