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
public abstract class Product extends BaseEntity {

    /**
     * Khóa chính của sản phẩm.
     * Được sinh tự động theo UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Tên sản phẩm.
     * Ví dụ: "Clean Code", "Tranh phong cảnh", "Tượng gỗ thủ công".
     *
     * Lưu ý:
     * Nếu sách có thể trùng tên giữa nhiều user/tác giả/tập,
     * không nên để unique = true.
     */
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    /**
     * Slug dùng cho URL SEO-friendly.
     * Thường được tự động sinh từ name.
     *
     * Ví dụ:
     * "Sản phẩm A" -> "san-pham-a"
     * "Clean Code" -> "clean-code"
     *
     * Lưu ý:
     * Nếu nhiều user có thể đăng sản phẩm cùng tên,
     * không nên để slug unique toàn hệ thống.
     */
    @Column(nullable = false, unique = true)
    private String slug;

    /**
     * Mô tả chi tiết sản phẩm.
     * Có thể chứa nội dung dài nên dùng kiểu TEXT trong database.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Giá gốc của sản phẩm.
     */
    private BigDecimal price;

    /**
     * Giá khuyến mại / giá giảm.
     * Nếu không có khuyến mại có thể để null.
     */
    private BigDecimal salePrice;

    /**
     * Số lượng tồn kho của sản phẩm.
     */
    private int quantity;

    /**
     * Mã SKU của sản phẩm.
     * SKU thường dùng để quản lý kho, tra cứu nội bộ.
     *
     * Ví dụ: BOOK-CLEAN-CODE-V1
     */
    private String sku;

    /**
     * Trạng thái bật/tắt sản phẩm.
     * true: sản phẩm đang được hiển thị/bán.
     * false: sản phẩm bị ẩn hoặc tạm ngừng bán.
     */
    private Boolean enable;

    /**
     * Đánh dấu sản phẩm nổi bật.
     * Dùng để hiển thị ở trang chủ, danh sách nổi bật, đề xuất...
     */
    private Boolean featured;

    /**
     * Số lượt xem sản phẩm.
     */
    private Long viewCount;

    /**
     * Đánh dấu sản phẩm số/digital.
     * Ví dụ: ebook, file PDF, tài liệu tải về.
     */
    private Boolean digital;

    /**
     * Ngày tạo sản phẩm.
     * Mặc định lấy ngày hiện tại khi khởi tạo object bằng builder.
     */
    @Builder.Default
    private LocalDate createDate = LocalDate.now();

    /**
     * Tiêu đề SEO của sản phẩm.
     * Dùng cho thẻ meta title trên trang chi tiết sản phẩm.
     */
    private String metaTitle;

    /**
     * Mô tả SEO của sản phẩm.
     * Dùng cho thẻ meta description.
     */
    @Column(columnDefinition = "TEXT")
    private String metaDescription;

    /**
     * Từ khóa SEO.
     * Ví dụ: "sách lập trình, clean code, java".
     */
    private String metaKeywords;

    /**
     * Loại sản phẩm theo enum ProductType.
     *
     * Ví dụ:
     * BOOK
     * PAINTING
     * ART_PRODUCT
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    /**
     * Danh mục của sản phẩm.
     * Ví dụ: Sách lập trình, Sách thiếu nhi, Tranh sơn dầu...
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    /**
     * Thương hiệu / nhà cung cấp / nhãn hàng của sản phẩm.
     * Với sách có thể dùng brand như một nhóm thương hiệu nếu hệ thống cần.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = true)
    private Brand brand;

    /**
     * Ảnh đại diện chính của sản phẩm.
     * Dùng làm thumbnail trên danh sách sản phẩm và trang chi tiết.
     */
    @OneToOne
    @JoinColumn(name = "image_id")
    private Image thumbnail;

    /**
     * Danh sách ảnh phụ của sản phẩm.
     * Ví dụ: ảnh bìa trước, bìa sau, ảnh chi tiết sản phẩm...
     */
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    /**
     * User tạo hoặc sở hữu sản phẩm.
     * Dùng để phân quyền dữ liệu theo user.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "discountcode_id", nullable = true) // Mã giảm giá có thể null
    private DiscountCode discountCode;
}