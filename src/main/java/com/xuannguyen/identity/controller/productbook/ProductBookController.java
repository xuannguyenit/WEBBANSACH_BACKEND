package com.xuannguyen.identity.controller.productbook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.productbook.ProductBookFullRequest;
import com.xuannguyen.identity.dto.request.productbook.ProductBookRequest;
import com.xuannguyen.identity.dto.request.productbook.ProductBookUpdateRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.productbook.ProductBookImportResponse;
import com.xuannguyen.identity.dto.response.productbook.ProductBookResponse;
import com.xuannguyen.identity.service.productbook.ProductBookService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product-books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductBookController {

    ProductBookService productBookService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ApiResponse<ProductBookResponse> create(@Valid @RequestBody ProductBookRequest request) {
        return ApiResponse.<ProductBookResponse>builder()
                .code(200)
                .message("Product Book Created")
                .result(productBookService.createProductBook(request))
                .build();
    }
    @GetMapping
    public ApiResponse<PageResponse<ProductBookResponse>> getAllProductBook(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<ProductBookResponse>>builder()
                .result(productBookService.getAllProductPagination(page, size))
                .build();
    }
    @PostMapping(
            value = "/full",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<ProductBookResponse> createFullProductBook(
            @RequestPart("request") String requestJson,

            @RequestPart(value = "thumbnail", required = false)
            MultipartFile thumbnail,

            @RequestPart(value = "images", required = false)
            List<MultipartFile> images,

            @RequestPart(value = "pdfFile", required = false)
            MultipartFile pdfFile
    ) throws JsonProcessingException {
        ProductBookFullRequest request =
                objectMapper.readValue(requestJson, ProductBookFullRequest.class);

        return ApiResponse.<ProductBookResponse>builder()
                .message("Create full product book successfully")
                .result(productBookService.createFullProductBook(
                        request,
                        thumbnail,
                        images,
                        pdfFile
                ))
                .build();
    }
    @PostMapping(
            value = "/import-excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<ProductBookImportResponse> importProductBooksFromExcel(
            @RequestPart("file") MultipartFile file
    ) {
        return ApiResponse.<ProductBookImportResponse>builder()
                .message("Import product books successfully")
                .result(productBookService.importProductBooksFromExcel(file))
                .build();
    }
}



