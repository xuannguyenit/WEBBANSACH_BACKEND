package com.xuannguyen.identity.dto.request.productbook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBookFullRequest {
    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal salePrice;

    private int quantity;

    private String sku;

    private Boolean enable;

    private Boolean featured;

    private Boolean digital;

    private String metaTitle;

    private String metaDescription;

    private String metaKeywords;

    private String categoryName;

    private String brandName;

    private String isbn;

    private Integer volumeNumber;

    private String bookAuthor;

    private String publisher;

    private Integer pageCount;

    private LocalDate publicationDate;

    private String language;

    private String bookFormat;
}
