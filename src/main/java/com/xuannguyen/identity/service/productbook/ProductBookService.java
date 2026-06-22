package com.xuannguyen.identity.service.productbook;

import com.xuannguyen.identity.dto.request.productbook.ProductBookCreateRequest;
import com.xuannguyen.identity.dto.request.productbook.ProductBookUpdateRequest;
import com.xuannguyen.identity.dto.response.productbook.ProductBookResponse;
import org.springframework.data.domain.Page;

public interface ProductBookService {
    ProductBookResponse create(ProductBookCreateRequest request);

    ProductBookResponse update(String id, ProductBookUpdateRequest request);

    ProductBookResponse getById(String id);

    ProductBookResponse getBySlug(String slug);

    Page<ProductBookResponse> getAll(int page, int size);

    void delete(String id);

    ProductBookResponse enable(String id);

    ProductBookResponse disable(String id);

    ProductBookResponse increaseViewCount(String id);
}
