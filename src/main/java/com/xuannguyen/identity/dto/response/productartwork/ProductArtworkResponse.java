package com.xuannguyen.identity.dto.response.productartwork;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductArtworkResponse {

    String id;

    String name;

    String slug;

    String description;

    BigDecimal price;

    BigDecimal salePrice;

    Integer quantity;

    String sku;

    Boolean enable;

    Boolean featured;

    Long viewCount;

    Boolean digital;

    LocalDate createDate;

    String type;

    String categoryId;

    String brandId;

    String thumbnailId;

    Long userId;

    List<String> listImage;

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
