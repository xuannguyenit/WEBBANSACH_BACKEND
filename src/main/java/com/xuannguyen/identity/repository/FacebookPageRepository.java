package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.FacebookPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacebookPageRepository extends JpaRepository<FacebookPage, String> {

    List<FacebookPage> findAllByUserId(Long userId);

    Optional<FacebookPage> findByIdAndUserId(String id, Long userId);

    Optional<FacebookPage> findByUserIdAndActiveTrue(Long userId);

    boolean existsByUserIdAndPageId(Long userId, String pageId);
}