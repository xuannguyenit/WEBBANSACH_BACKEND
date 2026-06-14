package com.xuannguyen.identity.service.categoryservice;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.categoryrequest.CategoryRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.categoryresponse.CategoryResponse;

public interface CategoryService {
    // thêm mới 1 category
    ApiResponse<Void> save(CategoryRequest request);
    // câp nhật 1 category
    ApiResponse<Void> update(String id, CategoryRequest request);
    // xóa 1 category    void delete(String id);
    ApiResponse<Void> changeStatus(String id);
    // chi tiết 1 category
    ApiResponse<CategoryResponse> getById(String id);
    // danh sách category
    ApiResponse<PageResponse<CategoryResponse>> getAll(int page, int size);
    // danh sách theo user
    ApiResponse<PageResponse<CategoryResponse>> getByUserId(Long userId, int page, int size);
}
