package com.xuannguyen.identity.dto.response.productbook;

import com.xuannguyen.identity.entity.FileResource;
import com.xuannguyen.identity.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBookFullResponse {

    /**
     * Tên sản phẩm.
     * Ví dụ: "Clean Code", "Tranh phong cảnh", "Tượng gỗ thủ công".
     *
     * Lưu ý:
     * Nếu sách có thể trùng tên giữa nhiều user/tác giả/tập,
     * không nên để unique = true.
     */
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

//    private String slug; slug tự sinh từ tên

    /**
     * Mô tả chi tiết sản phẩm.
     * Có thể chứa nội dung dài nên dùng kiểu TEXT trong database.
     */
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
     * Tiêu đề SEO của sản phẩm.
     * Dùng cho thẻ meta title trên trang chi tiết sản phẩm.
     */
    private String metaTitle;

    /**
     * Mô tả SEO của sản phẩm.
     * Dùng cho thẻ meta description.
     */
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
    private String type;

    /**
     * Danh mục của sản phẩm.
     * Ví dụ: Sách lập trình, Sách thiếu nhi, Tranh sơn dầu...
     */

    private String categoryName;

    /**
     * Thương hiệu / nhà cung cấp / nhãn hàng của sản phẩm.
     * Với sách có thể dùng brand như một nhóm thương hiệu nếu hệ thống cần.
     */

    private String brandName;

    /**
     * Ảnh đại diện chính của sản phẩm.
     * Dùng làm thumbnail trên danh sách sản phẩm và trang chi tiết.
     */

    private Image thumbnail;

    /**
     * Danh sách ảnh phụ của sản phẩm.
     * Ví dụ: ảnh bìa trước, bìa sau, ảnh chi tiết sản phẩm...
     */

    private List<Image> images ;

    /**
     * User tạo hoặc sở hữu sản phẩm.
     * Dùng để phân quyền dữ liệu theo user.
     */

//    private User user; lấy trực tiếp thông tin từ authen
    /**
     * Mã ISBN của sách.
     *
     * ISBN là mã định danh chuẩn quốc tế cho sách.
     * Một sách hoặc một phiên bản sách có thể có một ISBN riêng.
     *
     * Ví dụ:
     * 9780132350884
     *
     * Trường này có thể null nếu sách không có ISBN,
     * ví dụ tài liệu nội bộ, ebook tự xuất bản hoặc sách cũ.
     */
    private String isbn;

    /**
     * Số tập của sách.
     *
     * Ví dụ:
     * - Tập 1 -> volumeNumber = 1
     * - Tập 2 -> volumeNumber = 2
     * - Tập 10 -> volumeNumber = 10
     *
     * Dùng để phân biệt các sách cùng tên nhưng khác tập.
     */
    private Integer volumeNumber;

    /**
     * Tác giả của sách.
     *
     * Ví dụ:
     * "Robert C. Martin", "Nguyễn Nhật Ánh".
     *
     * Dùng để phân biệt các sách có cùng tên nhưng khác tác giả.
     */
    private String bookAuthor;

    /**
     * Nhà xuất bản của sách.
     *
     * Ví dụ:
     * "NXB Trẻ", "Prentice Hall", "O'Reilly Media".
     */
    private String publisher;

    /**
     * Số trang của sách.
     */
    private Integer pageCount;

    /**
     * Ngày xuất bản của sách.
     */
    private LocalDate publicationDate;

    /**
     * Ngôn ngữ của sách.
     *
     * Ví dụ:
     * "Vietnamese", "English", "Japanese".
     */
    private String language;

    /**
     * Định dạng sách.
     *
     * Ví dụ:
     * - Paperback: bìa mềm
     * - Hardcover: bìa cứng
     * - Ebook: sách điện tử
     * - PDF: file PDF
     */
    private String bookFormat;

    /**
     * File PDF đính kèm cho sách.
     *
     * Dùng khi sản phẩm là ebook, tài liệu số
     * hoặc có bản đọc thử dạng PDF.
     */
    private FileResource pdfFile;
}
