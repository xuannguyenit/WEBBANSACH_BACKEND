package com.xuannguyen.identity.dto.request.productpant;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPaintingRequest {
    String name;

    String description;

    BigDecimal price;

    BigDecimal salePrice;

    Integer quantity;

    String sku;

    Boolean enable;

    Boolean featured;

    String categoryName;

    String brandName;

    String thumbnailName;

    List<String> imageName;

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
