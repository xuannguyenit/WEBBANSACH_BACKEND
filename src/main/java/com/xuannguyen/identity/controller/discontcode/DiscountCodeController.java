package com.xuannguyen.identity.controller.discontcode;
import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.discontcode.DiscountCodeRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.discontcode.DiscountCodeResponse;
import com.xuannguyen.identity.service.discontcode.DiscountCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discount-codes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Discount Code", description = "API quản lý mã giảm giá")
@SecurityRequirement(name = "bearerAuth")
public class DiscountCodeController {

    DiscountCodeService discountCodeService;

    @PostMapping
    @Operation(summary = "Tạo mã giảm giá")
    public ApiResponse<DiscountCodeResponse> create(
            @RequestBody DiscountCodeRequest request
    ) {
        DiscountCodeResponse response = discountCodeService.create(request);

        return ApiResponse.<DiscountCodeResponse>builder()
                .code(200)
                .message("Tạo mã giảm giá thành công")
                .result(response)
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách mã giảm giá phân trang")
    public ApiResponse<PageResponse<DiscountCodeResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<DiscountCodeResponse> response = discountCodeService.getAll(page, size);

        return ApiResponse.<PageResponse<DiscountCodeResponse>>builder()
                .code(200)
                .message("Lấy danh sách mã giảm giá thành công")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết mã giảm giá theo id")
    public ApiResponse<DiscountCodeResponse> getById(
            @PathVariable String id
    ) {
        DiscountCodeResponse response = discountCodeService.getById(id);

        return ApiResponse.<DiscountCodeResponse>builder()
                .code(200)
                .message("Lấy chi tiết mã giảm giá thành công")
                .result(response)
                .build();
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Lấy chi tiết mã giảm giá theo code")
    public ApiResponse<DiscountCodeResponse> getByCode(
            @PathVariable String code
    ) {
        DiscountCodeResponse response = discountCodeService.getByCode(code);

        return ApiResponse.<DiscountCodeResponse>builder()
                .code(200)
                .message("Lấy mã giảm giá theo code thành công")
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật mã giảm giá")
    public ApiResponse<DiscountCodeResponse> update(
            @PathVariable String id,
            @RequestBody DiscountCodeRequest request
    ) {
        DiscountCodeResponse response = discountCodeService.update(id, request);

        return ApiResponse.<DiscountCodeResponse>builder()
                .code(200)
                .message("Cập nhật mã giảm giá thành công")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa mã giảm giá")
    public ApiResponse<Void> delete(
            @PathVariable String id
    ) {
        discountCodeService.delete(id);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa mã giảm giá thành công")
                .build();
    }
}