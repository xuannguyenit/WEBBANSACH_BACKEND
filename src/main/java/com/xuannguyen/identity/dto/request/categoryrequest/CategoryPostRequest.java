package com.xuannguyen.identity.dto.request.categoryrequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryPostRequest {
    private String name;
    private String imageId;
}
