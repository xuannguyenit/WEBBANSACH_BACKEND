package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Entity đại diện cho sản phẩm loại sách.
 *
 * Kế thừa từ Product nên các thông tin chung như name, price, slug,
 * category, thumbnail... được lưu ở bảng product.
 *
 * Các thông tin riêng của sách như isbn, tác giả, nhà xuất bản,
 * số trang... được lưu ở bảng product_book.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("BOOK")
@SuperBuilder
public class ProductBook extends Product {

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
    @OneToOne
    @JoinColumn(name = "pdf_file_id")
    private FileResource pdfFile;
}