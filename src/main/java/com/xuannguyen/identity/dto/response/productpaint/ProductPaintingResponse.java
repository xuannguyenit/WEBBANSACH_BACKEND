package com.xuannguyen.identity.dto.response.productpaint;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPaintingResponse {
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

    String medium;

    String style;

    String dimensions;

    Integer yearCreated;

    Boolean framed;

    Boolean original;

    Boolean certificateIncluded;
}
