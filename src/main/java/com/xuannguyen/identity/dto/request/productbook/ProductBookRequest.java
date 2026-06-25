package com.xuannguyen.identity.dto.request.productbook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProductBookRequest {

    private String name;

    private String description;

    /**
     * Giá gốc của sản phẩm.
     */
    private BigDecimal price;

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
    private String categoryName; // cần validate tên category nếu có . không nhập thì mặc định cate null
    private String brandName; // cần validate tên brand nếu có. không nhập thì mặc định brand null
    private String thumbnailName; // cần validate ảnh
    private List<String> imageName; // cần validate ảnh

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


    private String pdfFileName; // cần validate tên file sách
}
