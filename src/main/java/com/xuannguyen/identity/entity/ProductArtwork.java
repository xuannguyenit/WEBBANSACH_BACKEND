package com.xuannguyen.identity.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("ART_PRODUCT")
@SuperBuilder
public class ProductArtwork extends Product {

    /**
     * Tên nghệ nhân / tác giả.
     */
    private String artistName;

    /**
     * Loại sản phẩm nghệ thuật.
     * Ví dụ: sculpture, ceramic, handmade, decor...
     */
    private String artworkType;

    /**
     * Chất liệu: gỗ, gốm, đồng, đá, vải...
     */
    private String material;

    /**
     * Kích thước sản phẩm.
     */
    private String dimensions;

    /**
     * Cân nặng.
     */
    private String weight;

    /**
     * Sản phẩm làm thủ công hay không.
     */
    private Boolean handmade;

    /**
     * Sản phẩm giới hạn số lượng hay không.
     */
    private Boolean limitedEdition;

    /**
     * Số thứ tự phiên bản giới hạn.
     * Ví dụ: 5/100
     */
    private String editionNumber;

    /**
     * Xuất xứ.
     */
    private String origin;
}
