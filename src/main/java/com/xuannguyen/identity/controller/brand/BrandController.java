package com.xuannguyen.identity.controller.brand;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.brandrequest.BrandRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.brandresponse.BrandResponse;
import com.xuannguyen.identity.service.brandservice.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BrandController {
    private final BrandService brandService;
    @PostMapping
    @Operation(summary = "Tạo mới brand")
    public ApiResponse<Void> createBrand(@RequestBody BrandRequest request) {
        return brandService.save(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật brand")
    public ApiResponse<Void> updateBrand(
            @PathVariable String id,
            @RequestBody BrandRequest request
    ) {
        return brandService.update(id, request);
    }

    @PatchMapping("/{id}/change-status")
    @Operation(summary = "Chuyển đổi trạng thái brand")
    public ApiResponse<Void> changeStatus(@PathVariable String id) {
        return brandService.changeStatus(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết brand theo id")
    public ApiResponse<BrandResponse> getById(@PathVariable String id) {
        return brandService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách brand có phân trang")
    public ApiResponse<PageResponse<BrandResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return brandService.getAll(page, size);
    }
}
