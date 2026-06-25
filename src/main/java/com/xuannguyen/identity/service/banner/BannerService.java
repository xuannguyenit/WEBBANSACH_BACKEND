package com.xuannguyen.identity.service.banner;

import com.xuannguyen.identity.dto.request.banner.BannerRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.banner.BannerResponse;

public interface BannerService {

    BannerResponse create(BannerRequest request);

    BannerResponse getById(Long id);

    PageResponse<BannerResponse> getAll(int page, int size);

    PageResponse<BannerResponse> getAllActive(int page, int size);

    BannerResponse update(Long id, BannerRequest request);

    BannerResponse enable(Long id);

    BannerResponse disable(Long id);

    void delete(Long id);
}
