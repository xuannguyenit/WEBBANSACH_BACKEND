package com.xuannguyen.identity.dto.response.categoryresponse;

import com.xuannguyen.identity.dto.response.UserResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPostResponse {
    private Long id;
    private String name;
    private ImageResponse image;
    private UserResponse user;
}
