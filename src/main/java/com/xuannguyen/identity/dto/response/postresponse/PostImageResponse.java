package com.xuannguyen.identity.dto.response.postresponse;

import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageResponse {
    private Long id;
    private ImageResponse image;
    private Integer sortOrder;
    private String caption;
}
