package com.xuannguyen.identity.dto.request.postrequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageRequest {
    private String imageId;
    private Integer sortOrder;
    private String caption;
}
