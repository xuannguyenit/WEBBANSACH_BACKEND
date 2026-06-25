package com.xuannguyen.identity.service.discontcode;

import com.xuannguyen.identity.dto.request.discontcode.DiscountCodeRequest;
import com.xuannguyen.identity.dto.response.PageResponse;
import com.xuannguyen.identity.dto.response.discontcode.DiscountCodeResponse;

public interface DiscountCodeService {

    DiscountCodeResponse create(DiscountCodeRequest request);

    DiscountCodeResponse getById(String id);

    DiscountCodeResponse getByCode(String code);

    PageResponse<DiscountCodeResponse> getAll(int page, int size);

    DiscountCodeResponse update(String id, DiscountCodeRequest request);

    void delete(String id);
}
