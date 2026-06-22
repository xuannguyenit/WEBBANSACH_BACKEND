package com.xuannguyen.identity.service.categoryservice;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.categoryrequest.CategoryPostRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.categoryresponse.CategoryPostResponse;

public interface CategoryPostService {
    ApiResponse<Void> save(CategoryPostRequest request);

    ApiResponse<Void> update(Long id, CategoryPostRequest request);

    ApiResponse<CategoryPostResponse> getById(Long id);

    ApiResponse<PageResponse<CategoryPostResponse>> getAll(int page, int size);

    ApiResponse<PageResponse<CategoryPostResponse>> getByUserId(Long userId, int page, int size);
}
