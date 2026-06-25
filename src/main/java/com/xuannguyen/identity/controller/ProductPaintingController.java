package com.xuannguyen.identity.controller;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.productpant.ProductPaintingFullRequest;
import com.xuannguyen.identity.dto.request.productpant.ProductPaintingRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.productpaint.ProductPaintingResponse;
import com.xuannguyen.identity.service.productpaint.ProductPaintingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product-paintings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Product Painting", description = "API quản lý sản phẩm tranh")
@SecurityRequirement(name = "bearerAuth")
public class ProductPaintingController {

    ProductPaintingService productPaintingService;

    /**
     * Tạo sản phẩm tranh bằng JSON.
     *
     * Cách này dùng khi ảnh thumbnail, ảnh phụ đã được upload trước đó.
     * Request chỉ truyền tên thumbnailName và danh sách imageName.
     */
    @PostMapping
    @Operation(summary = "Tạo sản phẩm tranh")
    public ApiResponse<ProductPaintingResponse> createProductPainting(
            @RequestBody ProductPaintingRequest request
    ) {
        ProductPaintingResponse response = productPaintingService.createProductPainting(request);

        return ApiResponse.<ProductPaintingResponse>builder()
                .code(200)
                .message("Tạo sản phẩm tranh thành công")
                .result(response)
                .build();
    }

    /**
     * Tạo sản phẩm tranh full thông tin bằng multipart/form-data.
     *
     * Dùng khi muốn tạo tranh và upload ảnh trực tiếp trong cùng một request.
     *
     * Gồm:
     * - request: thông tin sản phẩm tranh dạng JSON
     * - thumbnailFile: ảnh đại diện chính
     * - imageFiles: danh sách ảnh phụ
     */
    @PostMapping(
            value = "/full",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Tạo sản phẩm tranh full thông tin kèm upload ảnh",
            description = "Tạo sản phẩm tranh và upload thumbnail, danh sách ảnh phụ trong cùng request"
    )
    public ApiResponse<ProductPaintingResponse> createFullProductPainting(
            @Parameter(
                    description = "Thông tin sản phẩm tranh dạng JSON",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductPaintingFullRequest.class)
                    )
            )
            @RequestPart("request") ProductPaintingFullRequest request,

            @Parameter(description = "Ảnh thumbnail của sản phẩm tranh")
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,

            @Parameter(description = "Danh sách ảnh phụ của sản phẩm tranh")
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) {
        ProductPaintingResponse response = productPaintingService.createFullProductPainting(
                request,
                thumbnailFile,
                imageFiles
        );

        return ApiResponse.<ProductPaintingResponse>builder()
                .code(200)
                .message("Tạo sản phẩm tranh full thông tin thành công")
                .result(response)
                .build();
    }

    /**
     * Lấy danh sách sản phẩm tranh có phân trang.
     *
     * page bắt đầu từ 1.
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách sản phẩm tranh phân trang")
    public ApiResponse<PageResponse<ProductPaintingResponse>> getAllProductPaintings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ProductPaintingResponse> response =
                productPaintingService.getAllProductPagination(page, size);

        return ApiResponse.<PageResponse<ProductPaintingResponse>>builder()
                .code(200)
                .message("Lấy danh sách sản phẩm tranh thành công")
                .result(response)
                .build();
    }

    /**
     * Lấy chi tiết sản phẩm tranh theo id.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết sản phẩm tranh")
    public ApiResponse<ProductPaintingResponse> getProductPaintingById(
            @PathVariable String id
    ) {
        ProductPaintingResponse response = productPaintingService.getById(id);

        return ApiResponse.<ProductPaintingResponse>builder()
                .code(200)
                .message("Lấy chi tiết sản phẩm tranh thành công")
                .result(response)
                .build();
    }

    /**
     * Bật trạng thái hiển thị/bán sản phẩm tranh.
     */
    @PatchMapping("/{id}/enable")
    @Operation(summary = "Bật sản phẩm tranh")
    public ApiResponse<ProductPaintingResponse> enableProductPainting(
            @PathVariable String id
    ) {
        ProductPaintingResponse response = productPaintingService.enable(id);

        return ApiResponse.<ProductPaintingResponse>builder()
                .code(200)
                .message("Bật sản phẩm tranh thành công")
                .result(response)
                .build();
    }

    /**
     * Tắt trạng thái hiển thị/bán sản phẩm tranh.
     */
    @PatchMapping("/{id}/disable")
    @Operation(summary = "Tắt sản phẩm tranh")
    public ApiResponse<ProductPaintingResponse> disableProductPainting(
            @PathVariable String id
    ) {
        ProductPaintingResponse response = productPaintingService.disable(id);

        return ApiResponse.<ProductPaintingResponse>builder()
                .code(200)
                .message("Tắt sản phẩm tranh thành công")
                .result(response)
                .build();
    }

    /**
     * Xóa sản phẩm tranh.
     *
     * Hiện tại service đang dùng delete vật lý.
     * Nếu bạn muốn soft delete thì nên đổi logic trong service thành set enable = false.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sản phẩm tranh")
    public ApiResponse<Void> deleteProductPainting(
            @PathVariable String id
    ) {
        productPaintingService.delete(id);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa sản phẩm tranh thành công")
                .build();
    }
}
