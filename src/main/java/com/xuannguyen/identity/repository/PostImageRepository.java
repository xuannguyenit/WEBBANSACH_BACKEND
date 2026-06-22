package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.Post;
import com.xuannguyen.identity.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPost(Post post);
    void deleteByPost(Post post);
}
