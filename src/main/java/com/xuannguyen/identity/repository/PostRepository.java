package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.CategoryPost;
import com.xuannguyen.identity.entity.Post;
import com.xuannguyen.identity.entity.PostStatus;
import com.xuannguyen.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    Optional<Post> findBySlug(String slug);

    Page<Post> findByUser(User user, Pageable pageable);

    Page<Post> findByCategoryPost(CategoryPost categoryPost, Pageable pageable);

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    Page<Post> findByEnable(Boolean enable, Pageable pageable);

    Page<Post> findByFeatured(Boolean featured, Pageable pageable);
}
