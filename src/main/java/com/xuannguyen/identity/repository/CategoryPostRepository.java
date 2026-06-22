package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.CategoryPost;
import com.xuannguyen.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryPostRepository extends JpaRepository<CategoryPost, Long> {
    boolean existsByNameAndUser(String name, User user);

    boolean existsByNameAndUserAndIdNot(String name, User user, Long id);

    Page<CategoryPost> findByUser(User user, Pageable pageable);
}
