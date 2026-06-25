package com.xuannguyen.identity.service.discontcode.impl;
import com.xuannguyen.identity.dto.request.discontcode.DiscountCodeRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.discontcode.DiscountCodeResponse;
import com.xuannguyen.identity.entity.DiscountCode;
import com.xuannguyen.identity.repository.DiscountCodeRepository;
import com.xuannguyen.identity.service.discontcode.DiscountCodeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DiscountCodeServiceImpl implements DiscountCodeService {

    DiscountCodeRepository discountCodeRepository;

    @Override
    @Transactional
    public DiscountCodeResponse create(DiscountCodeRequest request) {
        validateCreateRequest(request);

        String code = normalizeCode(request.getCode());

        if (discountCodeRepository.existsByCode(code)) {
            throw new RuntimeException("Mã giảm giá đã tồn tại: " + code);
        }

        DiscountCode discountCode = DiscountCode.builder()
                .code(code)
                .discountPercentage(request.getDiscountPercentage())
                .createDate(LocalDate.now())
                .build();

        DiscountCode savedDiscountCode = discountCodeRepository.save(discountCode);

        return toResponse(savedDiscountCode);
    }

    @Override
    public DiscountCodeResponse getById(String id) {
        if (isBlank(id)) {
            throw new RuntimeException("Id mã giảm giá không được để trống");
        }

        DiscountCode discountCode = discountCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        return toResponse(discountCode);
    }

    @Override
    public DiscountCodeResponse getByCode(String code) {
        if (isBlank(code)) {
            throw new RuntimeException("Code mã giảm giá không được để trống");
        }

        String normalizedCode = normalizeCode(code);

        DiscountCode discountCode = discountCodeRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại: " + normalizedCode));

        return toResponse(discountCode);
    }

    @Override
    public PageResponse<DiscountCodeResponse> getAll(int page, int size) {
        if (page < 1) {
            throw new RuntimeException("Page phải lớn hơn hoặc bằng 1");
        }

        if (size < 1) {
            throw new RuntimeException("Size phải lớn hơn hoặc bằng 1");
        }

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<DiscountCode> discountCodePage = discountCodeRepository.findAll(pageable);

        List<DiscountCodeResponse> responses = discountCodePage
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<DiscountCodeResponse>builder()
                .currentPage(page)
                .pageSize(size)
                .totalPages(discountCodePage.getTotalPages())
                .totalElements(discountCodePage.getTotalElements())
                .data(responses)
                .build();
    }

    @Override
    @Transactional
    public DiscountCodeResponse update(String id, DiscountCodeRequest request) {
        if (isBlank(id)) {
            throw new RuntimeException("Id mã giảm giá không được để trống");
        }

        validateUpdateRequest(request);

        DiscountCode discountCode = discountCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        String newCode = normalizeCode(request.getCode());

        if (!discountCode.getCode().equals(newCode)
                && discountCodeRepository.existsByCode(newCode)) {
            throw new RuntimeException("Mã giảm giá đã tồn tại: " + newCode);
        }

        discountCode.setCode(newCode);
        discountCode.setDiscountPercentage(request.getDiscountPercentage());

        DiscountCode savedDiscountCode = discountCodeRepository.save(discountCode);

        return toResponse(savedDiscountCode);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (isBlank(id)) {
            throw new RuntimeException("Id mã giảm giá không được để trống");
        }

        DiscountCode discountCode = discountCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

        discountCodeRepository.delete(discountCode);
    }

    private DiscountCodeResponse toResponse(DiscountCode discountCode) {
        return DiscountCodeResponse.builder()
                .id(discountCode.getId())
                .code(discountCode.getCode())
                .discountPercentage(discountCode.getDiscountPercentage())
                .createDate(discountCode.getCreateDate())
                .build();
    }

    private void validateCreateRequest(DiscountCodeRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        validateCode(request.getCode());

        validateDiscountPercentage(request.getDiscountPercentage());
    }

    private void validateUpdateRequest(DiscountCodeRequest request) {
        if (request == null) {
            throw new RuntimeException("Request không được null");
        }

        validateCode(request.getCode());

        validateDiscountPercentage(request.getDiscountPercentage());
    }

    private void validateCode(String code) {
        if (isBlank(code)) {
            throw new RuntimeException("Mã giảm giá không được để trống");
        }

        String normalizedCode = normalizeCode(code);

        if (normalizedCode.length() > 100) {
            throw new RuntimeException("Mã giảm giá không được vượt quá 100 ký tự");
        }

        if (!normalizedCode.matches("^[A-Z0-9_-]+$")) {
            throw new RuntimeException("Mã giảm giá chỉ được chứa chữ in hoa, số, dấu gạch dưới hoặc gạch ngang");
        }
    }

    private void validateDiscountPercentage(Long discountPercentage) {
        if (discountPercentage == null) {
            throw new RuntimeException("Phần trăm giảm giá không được để trống");
        }

        if (discountPercentage < 0) {
            throw new RuntimeException("Phần trăm giảm giá không được âm");
        }

        if (discountPercentage > 100) {
            throw new RuntimeException("Phần trăm giảm giá không được lớn hơn 100");
        }
    }

    private String normalizeCode(String code) {
        if (isBlank(code)) {
            return null;
        }

        return code.trim().toUpperCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}