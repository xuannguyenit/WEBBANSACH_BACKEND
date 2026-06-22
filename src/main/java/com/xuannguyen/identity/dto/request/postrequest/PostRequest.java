package com.xuannguyen.identity.dto.request.postrequest;

import com.xuannguyen.identity.entity.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    private String title;

    private String slug;

    private String content;

    private String description;

    private String author;

    private PostStatus status;

    private Boolean enable;

    private Boolean featured;

    private LocalDateTime publishedAt;

    private String metaTitle;

    private String metaDescription;

    private String metaKeywords;

    private Long categoryPostId;

    private String thumbnailId;

    private List<PostImageRequest> images;
}