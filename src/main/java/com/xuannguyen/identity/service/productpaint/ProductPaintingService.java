package com.xuannguyen.identity.service.productpaint;

import com.xuannguyen.identity.dto.request.productpant.ProductPaintingFullRequest;
import com.xuannguyen.identity.dto.request.productpant.ProductPaintingRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.productpaint.ProductPaintingResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductPaintingService {
    ProductPaintingResponse createProductPainting(ProductPaintingRequest request);

    ProductPaintingResponse createFullProductPainting(
            ProductPaintingFullRequest request,
            MultipartFile thumbnailFile,
            List<MultipartFile> imageFiles
    );

    PageResponse<ProductPaintingResponse> getAllProductPagination(int page, int size);

    ProductPaintingResponse getById(String id);

    ProductPaintingResponse enable(String id);

    ProductPaintingResponse disable(String id);

    void delete(String id);
}
