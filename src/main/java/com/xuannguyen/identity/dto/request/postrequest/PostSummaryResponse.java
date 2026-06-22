package com.xuannguyen.identity.dto.request.postrequest;

import com.xuannguyen.identity.dto.response.UserResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import com.xuannguyen.identity.entity.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryResponse {

    private Long id;

    private String title;

    private String slug;

    private String description;

    private String author;

    private PostStatus status;

    private Boolean enable;

    private Boolean featured;

    private Long viewCount;

    private LocalDateTime publishedAt;

    private UserResponse user;

    private PostCategoryResponse categoryPost;

    private ImageResponse thumbnail;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
