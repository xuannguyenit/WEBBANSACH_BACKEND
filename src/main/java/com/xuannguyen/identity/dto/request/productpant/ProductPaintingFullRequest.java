package com.xuannguyen.identity.dto.request.productpant;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPaintingFullRequest {
    String name;

    String description;

    BigDecimal price;

    BigDecimal salePrice;

    Integer quantity;

    String sku;

    Boolean enable;

    Boolean featured;

    Boolean digital;

    String categoryName;

    String brandName;

    String metaTitle;

    String metaDescription;

    String metaKeywords;

    String artistName;

    String medium;

    String style;

    String dimensions;

    Integer yearCreated;

    Boolean framed;

    Boolean original;

    Boolean certificateIncluded;
}
