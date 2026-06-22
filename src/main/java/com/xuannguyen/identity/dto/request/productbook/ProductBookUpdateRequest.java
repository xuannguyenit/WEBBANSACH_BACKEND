package com.xuannguyen.identity.dto.request.productbook;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class ProductBookUpdateRequest {

    String name;

    String slug;

    String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá không được âm")
    BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giá khuyến mại không được âm")
    BigDecimal salePrice;

    @Min(value = 0, message = "Số lượng không được âm")
    Integer quantity;

    String sku;

    Boolean enable;

    Boolean featured;

    Boolean digital;

    String metaTitle;

    String metaDescription;

    String metaKeywords;

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
