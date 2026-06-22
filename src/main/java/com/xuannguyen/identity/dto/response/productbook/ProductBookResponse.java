package com.xuannguyen.identity.dto.response.productbook;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductBookResponse {
    String id;

    String name;

    String slug;

    String description;

    BigDecimal price;

    BigDecimal salePrice;

    int quantity;

    String sku;

    Boolean enable;

    Boolean featured;

    Long viewCount;

    Boolean digital;

    LocalDate createDate;

    String metaTitle;

    String metaDescription;

    String metaKeywords;

    String type;

    String categoryId;

    String brandId;

    String thumbnailId;

    Long userId;

    String isbn;

    String bookAuthor;

    String publisher;

    Integer pageCount;

    LocalDate publicationDate;

    String language;

    String bookFormat;

    String pdfFileId;
}
