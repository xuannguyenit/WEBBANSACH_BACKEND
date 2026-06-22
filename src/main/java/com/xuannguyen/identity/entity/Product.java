package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "product_type")
public abstract class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug; // slug này tự động sinh ra từ name, ví dụ: "Sản phẩm A" -> "san-pham-a"

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;

    private BigDecimal salePrice;

    private int quantity;

    private String sku;

    private Boolean enable;

    private Boolean featured;

    private Long viewCount;

    private Boolean digital;
    @Builder.Default
    private LocalDate createDate = LocalDate.now();

    private String metaTitle;

    @Column(columnDefinition = "TEXT")
    private String metaDescription;

    private String metaKeywords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = true)
    private Brand brand;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image thumbnail;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

