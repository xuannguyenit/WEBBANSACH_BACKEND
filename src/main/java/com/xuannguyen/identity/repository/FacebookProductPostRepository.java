package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.FacebookProductPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FacebookProductPostRepository extends JpaRepository<FacebookProductPost, String> {
    List<FacebookProductPost> findAllByUserId(Long userId);

    List<FacebookProductPost> findAllByProductId(String productId);
}
