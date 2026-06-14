package com.xuannguyen.identity.controller.category;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.categoryrequest.CategoryRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.categoryresponse.CategoryResponse;
import com.xuannguyen.identity.service.categoryservice.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Tạo mới category")
    public ApiResponse<Void> createCategory(@RequestBody CategoryRequest request) {
        return categoryService.save(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật category")
    public ApiResponse<Void> updateCategory(
            @PathVariable String id,
            @RequestBody CategoryRequest request
    ) {
        return categoryService.update(id, request);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Chuyển đổi trạng thái category")
    public ApiResponse<Void> changeStatus(@PathVariable String id) {
        return categoryService.changeStatus(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết category theo id")
    public ApiResponse<CategoryResponse> getById(@PathVariable String id) {
        return categoryService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả category có phân trang")
    public ApiResponse<PageResponse<CategoryResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return categoryService.getAll(page, size);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lấy danh sách category theo user id")
    public ApiResponse<PageResponse<CategoryResponse>> getByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return categoryService.getByUserId(userId, page, size);
    }
}
