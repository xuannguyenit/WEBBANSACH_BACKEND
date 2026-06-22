package com.xuannguyen.identity.service.brandservice;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.brandrequest.BrandRequest;
import com.xuannguyen.identity.dto.request.categoryrequest.CategoryRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.brandresponse.BrandResponse;
import com.xuannguyen.identity.dto.response.categoryresponse.CategoryResponse;

public interface BrandService {
    ApiResponse<Void> save(BrandRequest request);
    // câp nhật 1 category
    ApiResponse<Void> update(String id, BrandRequest request);
    // xóa 1 category    void delete(String id);
    ApiResponse<Void> changeStatus(String id);
    // chi tiết 1 category
    ApiResponse<BrandResponse> getById(String id);
    // danh sách category
    ApiResponse<PageResponse<BrandResponse>> getAll(int page, int size);
}
