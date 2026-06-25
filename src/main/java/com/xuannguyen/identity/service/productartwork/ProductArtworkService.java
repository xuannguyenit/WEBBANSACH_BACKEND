package com.xuannguyen.identity.service.productartwork;

import com.xuannguyen.identity.dto.request.productartwork.ProductArtworkFullRequest;
import com.xuannguyen.identity.dto.request.productartwork.ProductArtworkRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.productartwork.ProductArtworkResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductArtworkService {
    ProductArtworkResponse createProductArtwork(ProductArtworkRequest request);

    ProductArtworkResponse createFullProductArtwork(
            ProductArtworkFullRequest request,
            MultipartFile thumbnailFile,
            List<MultipartFile> imageFiles
    );

    PageResponse<ProductArtworkResponse> getAllProductPagination(int page, int size);

    ProductArtworkResponse getById(String id);

    ProductArtworkResponse enable(String id);

    ProductArtworkResponse disable(String id);

    void delete(String id);
}
