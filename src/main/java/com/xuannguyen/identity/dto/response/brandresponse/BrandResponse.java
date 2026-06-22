package com.xuannguyen.identity.dto.response.brandresponse;

import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandResponse {
    private String id;
    private String name;
    private boolean enable;
    private ImageResponse avatar;
}
