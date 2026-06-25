package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class FacebookPage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * User sở hữu cấu hình fanpage.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Page ID trên Facebook.
     */
    @Column(name = "page_id", nullable = false)
    private String pageId;

    /**
     * Tên fanpage.
     */
    @Column(name = "page_name")
    private String pageName;

    /**
     * Page access token.
     *
     * Lưu ý: production nên mã hóa token trước khi lưu DB.
     */
    @Column(name = "page_access_token", columnDefinition = "TEXT", nullable = false)
    private String pageAccessToken;

    /**
     * Trạng thái cấu hình.
     */
    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
