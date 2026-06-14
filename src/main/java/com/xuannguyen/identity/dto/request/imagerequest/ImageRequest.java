package com.xuannguyen.identity.dto.request.imagerequest;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageRequest {
    private String name;
    private String type;
    private Long size;
    private byte[] data;
}
