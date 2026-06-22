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
@DiscriminatorValue("PAINTING")
@SuperBuilder
public class ProductPainting extends Product {

    /**
     * Tên họa sĩ.
     */
    private String artistName;

    /**
     * Chất liệu vẽ: sơn dầu, acrylic, màu nước...
     */
    private String medium;

    /**
     * Phong cách: trừu tượng, hiện thực, phong cảnh...
     */
    private String style;

    /**
     * Kích thước tranh.
     * Ví dụ: 60x90cm
     */
    private String dimensions;

    /**
     * Năm sáng tác.
     */
    private Integer yearCreated;

    /**
     * Tranh có khung hay chưa.
     */
    private Boolean framed;

    /**
     * Có phải bản gốc không.
     */
    private Boolean original;

    /**
     * Có giấy chứng nhận không.
     */
    private Boolean certificateIncluded;
}
