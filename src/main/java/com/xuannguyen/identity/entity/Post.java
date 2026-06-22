package com.xuannguyen.identity.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Post extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String author;
    @Enumerated(EnumType.STRING)
    private PostStatus status;
    private Boolean enable;
    private Boolean featured;
    private Long viewCount;
    private LocalDateTime publishedAt;
    private String metaTitle;
    @Column(columnDefinition = "TEXT")
    private String metaDescription;
    private String metaKeywords;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_post_id")
    private CategoryPost categoryPost;
    /**
     * Ảnh đại diện bài viết
     */
    @OneToOne
    @JoinColumn(name = "image_id")
    private Image thumbnail;

    /**
     * Danh sách ảnh phụ
     */
    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PostImage> images = new ArrayList<>();

}
