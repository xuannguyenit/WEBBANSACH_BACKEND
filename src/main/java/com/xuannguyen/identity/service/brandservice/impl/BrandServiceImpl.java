package com.xuannguyen.identity.service.brandservice.impl;

import com.xuannguyen.identity.dto.request.ApiResponse;
import com.xuannguyen.identity.dto.request.brandrequest.BrandRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.brandresponse.BrandResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import com.xuannguyen.identity.entity.Brand;
import com.xuannguyen.identity.entity.Image;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.BrandRepository;
import com.xuannguyen.identity.repository.ImageRepository;
import com.xuannguyen.identity.service.brandservice.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ImageRepository imageRepository;

    private ImageResponse toImageResponse(Image image) {
        if (image == null) {
            return null;
        }

        return ImageResponse.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .size(image.getSize())
                .url("/images/" + image.getId() + "/view")
                .build();
    }

    private BrandResponse toBrandResponse(Brand brand) {
        if (brand == null) {
            return null;
        }

        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .enable(brand.isEnable())
                .avatar(toImageResponse(brand.getAvatar()))
                .build();
    }

    @Override
    public ApiResponse<Void> save(BrandRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.INVALID_BRAND_NAME);
        }

        String brandName = request.getName().trim();

        // Kiểm tra tên brand đã tồn tại chưa
        if (brandRepository.existsByName(brandName)) {
            throw new AppException(ErrorCode.BRAND_NAME_ALREADY_EXISTSED);
        }

        Image image = null;

        // Nếu có truyền avatarId thì kiểm tra ảnh có tồn tại không
        if (request.getAvatarId() != null && !request.getAvatarId().isBlank()) {
            image = imageRepository.findById(request.getAvatarId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
        }

        Brand brand = Brand.builder()
                .name(brandName)
                .avatar(image)
                .enable(true)
                .build();

        brandRepository.save(brand);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Brand created successfully")
                .build();
    }

    @Override
    public ApiResponse<Void> update(String id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));

        if (request.getName() == null || request.getName().isBlank()) {
            throw new AppException(ErrorCode.INVALID_BRAND_NAME);
        }

        String brandName = request.getName().trim();

        // Kiểm tra tên brand có bị trùng với brand khác không
        if (brandRepository.existsByNameAndIdNot(brandName, id)) {
            throw new AppException(ErrorCode.BRAND_NAME_ALREADY_EXISTSED);
        }

        // Nếu có truyền avatarId thì cập nhật avatar
        if (request.getAvatarId() != null && !request.getAvatarId().isBlank()) {
            Image image = imageRepository.findById(request.getAvatarId())
                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));

            brand.setAvatar(image);
        }

        brand.setName(brandName);

        brandRepository.save(brand);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Brand updated successfully")
                .build();
    }

    @Override
    public ApiResponse<Void> changeStatus(String id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));

        // Đảo trạng thái true -> false, false -> true
        brand.setEnable(!brand.isEnable());

        brandRepository.save(brand);

        return ApiResponse.<Void>builder()
                .code(200)
                .message("Brand status changed successfully")
                .build();
    }

    @Override
    public ApiResponse<BrandResponse> getById(String id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));

        return ApiResponse.<BrandResponse>builder()
                .code(200)
                .message("Get brand successfully")
                .result(toBrandResponse(brand))
                .build();
    }

    @Override
    public ApiResponse<PageResponse<BrandResponse>> getAll(int page, int size) {
        if (page < 1) {
            page = 1;
        }

        if (size < 1) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Brand> brandPage = brandRepository.findAll(pageable);

        List<BrandResponse> brandResponses = brandPage.getContent()
                .stream()
                .map(this::toBrandResponse)
                .toList();

        PageResponse<BrandResponse> pageResponse = PageResponse.<BrandResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(brandPage.getTotalPages())
                .totalElements(brandPage.getTotalElements())
                .data(brandResponses)
                .build();

        return ApiResponse.<PageResponse<BrandResponse>>builder()
                .code(200)
                .message("Get brands successfully")
                .result(pageResponse)
                .build();
    }
}
