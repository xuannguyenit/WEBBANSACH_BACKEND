package com.xuannguyen.identity.dto.response.postresponse;

import com.xuannguyen.identity.dto.request.postrequest.PostCategoryResponse;
import com.xuannguyen.identity.dto.response.UserResponse;
import com.xuannguyen.identity.dto.response.imageresponse.ImageResponse;
import com.xuannguyen.identity.entity.PostStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostResponse {
    private Long id;

    private String title;

    private String slug;

    private String content;

    private String description;

    private String author;

    private PostStatus status;

    private Boolean enable;

    private Boolean featured;

    private Long viewCount;

    private LocalDateTime publishedAt;

    private String metaTitle;

    private String metaDescription;

    private String metaKeywords;

    private UserResponse user;

    private PostCategoryResponse categoryPost;

    private ImageResponse thumbnail;

    private List<PostImageResponse> images;

    /**
     * Nếu BaseEntity của bạn có createdAt, updatedAt thì giữ lại.
     * Nếu không có thì xóa 2 field này.
     */
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
