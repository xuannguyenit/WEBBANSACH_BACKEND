package com.xuannguyen.identity.controller.banner;
import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.banner.BannerRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.banner.BannerResponse;
import com.xuannguyen.identity.service.banner.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Banner", description = "API quản lý banner")
@SecurityRequirement(name = "bearerAuth")
public class BannerController {

    BannerService bannerService;

    @PostMapping
    @Operation(summary = "Tạo banner")
    public ApiResponse<BannerResponse> create(
            @RequestBody BannerRequest request
    ) {
        BannerResponse response = bannerService.create(request);

        return ApiResponse.<BannerResponse>builder()
                .code(200)
                .message("Tạo banner thành công")
                .result(response)
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách banner phân trang")
    public ApiResponse<PageResponse<BannerResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<BannerResponse> response = bannerService.getAll(page, size);

        return ApiResponse.<PageResponse<BannerResponse>>builder()
                .code(200)
                .message("Lấy danh sách banner thành công")
                .result(response)
                .build();
    }

    @GetMapping("/active")
    @Operation(summary = "Lấy danh sách banner đang active")
    public ApiResponse<PageResponse<BannerResponse>> getAllActive(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<BannerResponse> response = bannerService.getAllActive(page, size);

        return ApiResponse.<PageResponse<BannerResponse>>builder()
                .code(200)
                .message("Lấy danh sách banner active thành công")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết banner")
    public ApiResponse<BannerResponse> getById(
            @PathVariable Long id
    ) {
        BannerResponse response = bannerService.getById(id);

        return ApiResponse.<BannerResponse>builder()
                .code(200)
                .message("Lấy chi tiết banner thành công")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật banner")
    public ApiResponse<BannerResponse> update(
            @PathVariable Long id,
            @RequestBody BannerRequest request
    ) {
        BannerResponse response = bannerService.update(id, request);

        return ApiResponse.<BannerResponse>builder()
                .code(200)
                .message("Cập nhật banner thành công")
                .result(response)
                .build();
    }

    @PatchMapping("/{id}/enable")
    @Operation(summary = "Bật banner")
    public ApiResponse<BannerResponse> enable(
            @PathVariable Long id
    ) {
        BannerResponse response = bannerService.enable(id);

        return ApiResponse.<BannerResponse>builder()
                .code(200)
                .message("Bật banner thành công")
                .result(response)
                .build();
    }

    @PatchMapping("/{id}/disable")
    @Operation(summary = "Tắt banner")
    public ApiResponse<BannerResponse> disable(
            @PathVariable Long id
    ) {
        BannerResponse response = bannerService.disable(id);

        return ApiResponse.<BannerResponse>builder()
                .code(200)
                .message("Tắt banner thành công")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa banner")
    public ApiResponse<Void> delete(
            @PathVariable Long id
    ) {
        bannerService.delete(id);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa banner thành công")
                .build();
    }
}
