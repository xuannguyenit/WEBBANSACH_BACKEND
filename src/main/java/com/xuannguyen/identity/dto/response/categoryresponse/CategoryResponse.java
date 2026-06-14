package com.xuannguyen.identity.dto.response.categoryresponse;

import com.xuannguyen.identity.dto.response.UserResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private  String id;
    private String name;
    private boolean enable;
    private UserResponse user;
    private ImageResponse avatar;
}
