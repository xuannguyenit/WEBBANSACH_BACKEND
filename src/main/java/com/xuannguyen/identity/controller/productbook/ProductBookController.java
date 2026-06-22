package com.xuannguyen.identity.controller.productbook;

import com.xuannguyen.identity.dto.request.productbook.ProductBookCreateRequest;
import com.xuannguyen.identity.dto.request.productbook.ProductBookUpdateRequest;
import com.xuannguyen.identity.dto.response.productbook.ProductBookResponse;
import com.xuannguyen.identity.service.productbook.ProductBookService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductBookController {

    ProductBookService productBookService;

    @PostMapping
    public ProductBookResponse create(@Valid @RequestBody ProductBookCreateRequest request) {
        return productBookService.create(request);
    }

    @PutMapping("/{id}")
    public ProductBookResponse update(
            @PathVariable String id,
            @Valid @RequestBody ProductBookUpdateRequest request
    ) {
        return productBookService.update(id, request);
    }

    @GetMapping("/{id}")
    public ProductBookResponse getById(@PathVariable String id) {
        return productBookService.getById(id);
    }

    @GetMapping("/slug/{slug}")
    public ProductBookResponse getBySlug(@PathVariable String slug) {
        return productBookService.getBySlug(slug);
    }

    @GetMapping
    public Page<ProductBookResponse> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productBookService.getAll(page, size);
    }

    @PatchMapping("/{id}/enable")
    public ProductBookResponse enable(@PathVariable String id) {
        return productBookService.enable(id);
    }

    @PatchMapping("/{id}/disable")
    public ProductBookResponse disable(@PathVariable String id) {
        return productBookService.disable(id);
    }

    @PatchMapping("/{id}/view")
    public ProductBookResponse increaseViewCount(@PathVariable String id) {
        return productBookService.increaseViewCount(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        productBookService.delete(id);
    }
}