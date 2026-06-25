package com.xuannguyen.identity.service.banner.impl;
import com.xuannguyen.identity.dto.request.banner.BannerRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.banner.BannerResponse;
import com.xuannguyen.identity.entity.Banner;
import com.xuannguyen.identity.entity.Image;
import com.xuannguyen.identity.exception.AppException;
import com.xuannguyen.identity.exception.ErrorCode;
import com.xuannguyen.identity.repository.BannerRepository;
import com.xuannguyen.identity.repository.ImageRepository;
import com.xuannguyen.identity.service.banner.BannerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BannerServiceImpl implements BannerService {

    BannerRepository bannerRepository;

    ImageRepository imageRepository;

    @Override
    @Transactional
    public BannerResponse create(BannerRequest request) {
        validateCreateRequest(request);

        Image image = validateImage(request.getImageId());

        Banner banner = Banner.builder()
                .title(request.getTitle().trim())
                .description(trimToNull(request.getDescription()))
                .redirectUrl(trimToNull(request.getRedirectUrl()))
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .active(request.getActive() != null ? request.getActive() : true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .image(image)
                .build();

        Banner savedBanner = bannerRepository.save(banner);

        return toResponse(savedBanner);
    }

    @Override
    public BannerResponse getById(Long id) {
        if (id == null) {
            throw new RuntimeException("Id banner không được để trống");
        }

        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner không tồn tại"));

        return toResponse(banner);
    }

    @Override
    public PageResponse<BannerResponse> getAll(int page, int size) {
        validatePageRequest(page, size);

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                Sort.by(Sort.Direction.ASC, "sortOrder")
        );

        Page<Banner> bannerPage = bannerRepository.findAll(pageable);

        List<BannerResponse> responses = bannerPage
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<BannerResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(bannerPage.getTotalPages())
                .totalElements(bannerPage.getTotalElements())
                .data(responses)
                .build();
    }

    @Override
    public PageResponse<BannerResponse> getAllActive(int page, int size) {
        validatePageRequest(page, size);

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Banner> bannerPage = bannerRepository.findByActiveTrueOrderBySortOrderAsc(pageable);

        List<BannerResponse> responses = bannerPage
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<BannerResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(bannerPage.getTotalPages())
                .totalElements(bannerPage.getTotalElements())
                .data(responses)
                .build();
    }

    @Override
    @Transactional
    public BannerResponse update(Long id, BannerRequest request) {
        if (id == null) {
            throw new RuntimeException("Id banner không được để trống");
        }

        validateUpdateRequest(request);

        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner không tồn tại"));

        Image image = validateImage(request.getImageId());

        banner.setTitle(request.getTitle().trim());
        banner.setDescription(trimToNull(request.getDescription()));
        banner.setRedirectUrl(trimToNull(request.getRedirectUrl()));
        banner.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        banner.setActive(request.getActive() != null ? request.getActive() : banner.getActive());
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());
        banner.setImage(image);

        Banner savedBanner = bannerRepository.save(banner);

        return toResponse(savedBanner);
    }

    @Override
    @Transactional
    public BannerResponse enable(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner không tồn tại"));

        banner.setActive(true);

        return toResponse(bannerRepository.save(banner));
    }

    @Override
    @Transactional
    public BannerResponse disable(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner không tồn tại"));

        banner.setActive(false);

        return toResponse(bannerRepository.save(banner));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new RuntimeException("Id banner không được để trống");
        }

        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner không tồn tại"));

        bannerRepository.delete(banner);
    }

    private BannerResponse toResponse(Banner banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .description(banner.getDescription())
                .redirectUrl(banner.getRedirectUrl())
                .sortOrder(banner.getSortOrder())
                .active(banner.getActive())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .imageId(banner.getImage() != null ? banner.getImage().getId() : null)
                .imageName(banner.getImage() != null ? banner.getImage().getName() : null)
                .imageUrl(banner.getImage() != null ? "/images/" + banner.getImage().getId() + "/view" : null)
                .build();
    }

    private void validateCreateRequest(BannerRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        validateCommonRequest(request);
    }

    private void validateUpdateRequest(BannerRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        validateCommonRequest(request);
    }

    private void validateCommonRequest(BannerRequest request) {
        if (isBlank(request.getTitle())) {
            throw new RuntimeException("Tiêu đề banner không được để trống");
        }

        if (request.getTitle().trim().length() > 255) {
            throw new RuntimeException("Tiêu đề banner không được vượt quá 255 ký tự");
        }

        if (request.getSortOrder() != null && request.getSortOrder() < 0) {
            throw new RuntimeException("Thứ tự hiển thị không được âm");
        }

        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("Ngày kết thúc không được nhỏ hơn ngày bắt đầu");
        }

        if (!isBlank(request.getImageId())) {
            validateImage(request.getImageId());
        }
    }

    private Image validateImage(String imageId) {
        if (isBlank(imageId)) {
            return null;
        }

        return imageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_EXISTED));
    }

    private void validatePageRequest(int page, int size) {
        if (page < 1) {
            throw new RuntimeException("Page phải lớn hơn hoặc bằng 1");
        }

        if (size < 1) {
            throw new RuntimeException("Size phải lớn hơn hoặc bằng 1");
        }
    }

    private String trimToNull(String value) {
        if (isBlank(value)) {
            return null;
        }

        return value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}