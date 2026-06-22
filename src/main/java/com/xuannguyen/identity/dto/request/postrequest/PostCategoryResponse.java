package com.xuannguyen.identity.dto.request.postrequest;

import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCategoryResponse {
    private Long id;
    private String name;
    private ImageResponse image;
}
