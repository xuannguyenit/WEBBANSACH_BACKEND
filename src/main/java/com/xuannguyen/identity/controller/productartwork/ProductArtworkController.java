package com.xuannguyen.identity.controller.productartwork;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.productartwork.ProductArtworkFullRequest;
import com.xuannguyen.identity.dto.request.productartwork.ProductArtworkRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.productartwork.ProductArtworkResponse;
import com.xuannguyen.identity.service.productartwork.ProductArtworkService;
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
@RequestMapping("/product-artworks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Product Artwork", description = "API quản lý sản phẩm nghệ thuật")
@SecurityRequirement(name = "bearerAuth")
public class ProductArtworkController {

    ProductArtworkService productArtworkService;

    @PostMapping
    @Operation(summary = "Tạo sản phẩm nghệ thuật")
    public ApiResponse<ProductArtworkResponse> createProductArtwork(
            @RequestBody ProductArtworkRequest request
    ) {
        ProductArtworkResponse response = productArtworkService.createProductArtwork(request);

        return ApiResponse.<ProductArtworkResponse>builder()
                .code(200)
                .message("Tạo sản phẩm nghệ thuật thành công")
                .result(response)
                .build();
    }

    @PostMapping(
            value = "/full",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Tạo sản phẩm nghệ thuật full thông tin kèm upload ảnh",
            description = "Tạo sản phẩm nghệ thuật và upload thumbnail, danh sách ảnh phụ trong cùng request"
    )
    public ApiResponse<ProductArtworkResponse> createFullProductArtwork(
            @Parameter(
                    description = "Thông tin sản phẩm nghệ thuật dạng JSON",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductArtworkFullRequest.class)
                    )
            )
            @RequestPart("request") ProductArtworkFullRequest request,

            @Parameter(description = "Ảnh thumbnail của sản phẩm nghệ thuật")
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,

            @Parameter(description = "Danh sách ảnh phụ của sản phẩm nghệ thuật")
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
    ) {
        ProductArtworkResponse response = productArtworkService.createFullProductArtwork(
                request,
                thumbnailFile,
                imageFiles
        );

        return ApiResponse.<ProductArtworkResponse>builder()
                .code(200)
                .message("Tạo sản phẩm nghệ thuật full thông tin thành công")
                .result(response)
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách sản phẩm nghệ thuật phân trang")
    public ApiResponse<PageResponse<ProductArtworkResponse>> getAllProductArtworks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ProductArtworkResponse> response =
                productArtworkService.getAllProductPagination(page, size);

        return ApiResponse.<PageResponse<ProductArtworkResponse>>builder()
                .code(200)
                .message("Lấy danh sách sản phẩm nghệ thuật thành công")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết sản phẩm nghệ thuật")
    public ApiResponse<ProductArtworkResponse> getProductArtworkById(
            @PathVariable String id
    ) {
        ProductArtworkResponse response = productArtworkService.getById(id);

        return ApiResponse.<ProductArtworkResponse>builder()
                .code(200)
                .message("Lấy chi tiết sản phẩm nghệ thuật thành công")
                .result(response)
                .build();
    }

    @PatchMapping("/{id}/enable")
    @Operation(summary = "Bật sản phẩm nghệ thuật")
    public ApiResponse<ProductArtworkResponse> enableProductArtwork(
            @PathVariable String id
    ) {
        ProductArtworkResponse response = productArtworkService.enable(id);

        return ApiResponse.<ProductArtworkResponse>builder()
                .code(200)
                .message("Bật sản phẩm nghệ thuật thành công")
                .result(response)
                .build();
    }

    @PatchMapping("/{id}/disable")
    @Operation(summary = "Tắt sản phẩm nghệ thuật")
    public ApiResponse<ProductArtworkResponse> disableProductArtwork(
            @PathVariable String id
    ) {
        ProductArtworkResponse response = productArtworkService.disable(id);

        return ApiResponse.<ProductArtworkResponse>builder()
                .code(200)
                .message("Tắt sản phẩm nghệ thuật thành công")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sản phẩm nghệ thuật")
    public ApiResponse<Void> deleteProductArtwork(
            @PathVariable String id
    ) {
        productArtworkService.delete(id);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa sản phẩm nghệ thuật thành công")
                .build();
    }
}
