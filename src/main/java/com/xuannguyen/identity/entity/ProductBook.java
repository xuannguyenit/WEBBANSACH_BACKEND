package com.xuannguyen.identity.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("BOOK")
@SuperBuilder
public class ProductBook extends Product {

    private String isbn;

    private String bookAuthor;

    private String publisher;

    private Integer pageCount;

    private LocalDate publicationDate;

    private String language;

    private String bookFormat;

    /**
     * File PDF đính kèm cho sách.
     */
    @OneToOne
    @JoinColumn(name = "pdf_file_id")
    private FileResource pdfFile;
}
