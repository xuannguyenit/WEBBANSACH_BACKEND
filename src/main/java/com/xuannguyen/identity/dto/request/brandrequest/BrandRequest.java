package com.xuannguyen.identity.dto.request.brandrequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandRequest {
    private String name;
    private boolean enable;
    private String avatarId;
}
