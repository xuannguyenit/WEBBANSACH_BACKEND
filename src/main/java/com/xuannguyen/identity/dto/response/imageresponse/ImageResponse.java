package com.xuannguyen.identity.dto.response.imageresponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
    private String id;
    private String name;
    private String type;
    private Long size;
    private String url;
}
