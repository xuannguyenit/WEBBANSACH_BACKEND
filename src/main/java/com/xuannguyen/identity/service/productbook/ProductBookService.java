package com.xuannguyen.identity.service.productbook;

import com.xuannguyen.identity.dto.request.productbook.ProductBookFullRequest;
import com.xuannguyen.identity.dto.request.productbook.ProductBookRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.productbook.ProductBookImportResponse;
import com.xuannguyen.identity.dto.response.productbook.ProductBookResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ProductBookService {

    ProductBookResponse createProductBook(ProductBookRequest request);

    PageResponse<ProductBookResponse> getAllProductPagination(int page, int size);

    void delete(String id);

    ProductBookResponse enable(String id);

    ProductBookResponse disable(String id);

    ProductBookResponse createFullProductBook(
            ProductBookFullRequest request,
            MultipartFile thumbnail,
            List<MultipartFile> images,
            MultipartFile pdfFile
    );

    ProductBookImportResponse importProductBooksFromExcel(MultipartFile file);

}
