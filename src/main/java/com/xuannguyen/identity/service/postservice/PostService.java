package com.xuannguyen.identity.service.postservice;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.postrequest.PostRequest;
import com.xuannguyen.identity.dto.request.postrequest.PostSummaryResponse;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.postresponse.PostResponse;

public interface PostService {
    ApiResponse<Void> save(PostRequest request);

    ApiResponse<Void> update(Long id, PostRequest request);

    ApiResponse<Void> changeStatus(Long id);

    ApiResponse<Void> delete(Long id);

    ApiResponse<PostResponse> getById(Long id);

    ApiResponse<PostResponse> getBySlug(String slug);

    ApiResponse<PageResponse<PostSummaryResponse>> getAll(int page, int size);

    ApiResponse<PageResponse<PostSummaryResponse>> getByUserId(Long userId, int page, int size);

    ApiResponse<PageResponse<PostSummaryResponse>> getByCategoryPostId(Long categoryPostId, int page, int size);

    ApiResponse<Void> increaseView(Long id);
}
