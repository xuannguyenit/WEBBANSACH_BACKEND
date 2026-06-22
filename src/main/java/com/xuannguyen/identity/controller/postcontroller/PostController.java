package com.xuannguyen.identity.controller.postcontroller;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.postrequest.PostRequest;
import com.xuannguyen.identity.dto.request.postrequest.PostSummaryResponse;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.postresponse.PostResponse;
import com.xuannguyen.identity.service.postservice.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "Tạo mới bài viết")
    public ApiResponse<Void> createPost(@RequestBody PostRequest request) {
        return postService.save(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật bài viết")
    public ApiResponse<Void> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequest request
    ) {
        return postService.update(id, request);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Chuyển đổi trạng thái bật/tắt bài viết")
    public ApiResponse<Void> changeStatus(@PathVariable Long id) {
        return postService.changeStatus(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa bài viết")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        return postService.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết bài viết theo id")
    public ApiResponse<PostResponse> getById(@PathVariable Long id) {
        return postService.getById(id);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Lấy chi tiết bài viết theo slug")
    public ApiResponse<PostResponse> getBySlug(@PathVariable String slug) {
        return postService.getBySlug(slug);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách bài viết có phân trang")
    public ApiResponse<PageResponse<PostSummaryResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return postService.getAll(page, size);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lấy danh sách bài viết theo user id")
    public ApiResponse<PageResponse<PostSummaryResponse>> getByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return postService.getByUserId(userId, page, size);
    }

    @GetMapping("/category-post/{categoryPostId}")
    @Operation(summary = "Lấy danh sách bài viết theo danh mục bài viết")
    public ApiResponse<PageResponse<PostSummaryResponse>> getByCategoryPostId(
            @PathVariable Long categoryPostId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return postService.getByCategoryPostId(categoryPostId, page, size);
    }

    @PatchMapping("/{id}/increase-view")
    @Operation(summary = "Tăng lượt xem bài viết")
    public ApiResponse<Void> increaseView(@PathVariable Long id) {
        return postService.increaseView(id);
    }
}
