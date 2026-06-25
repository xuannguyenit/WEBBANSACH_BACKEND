package com.xuannguyen.identity.dto.request.productartwork;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductArtworkRequest {

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

    String artworkType;

    String material;

    String dimensions;

    String weight;

    Boolean handmade;

    Boolean limitedEdition;

    String editionNumber;

    String origin;
}