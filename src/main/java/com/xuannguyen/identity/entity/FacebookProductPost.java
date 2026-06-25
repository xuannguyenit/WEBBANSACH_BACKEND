package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacebookProductPost {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "facebook_page_config_id")
    private String facebookPageConfigId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "page_id")
    private String pageId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "facebook_post_id")
    private String facebookPostId;

    @Column(name = "status")
    private String status;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;
}
