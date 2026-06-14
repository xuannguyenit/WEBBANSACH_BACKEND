package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.Category;
import com.xuannguyen.identity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,String> {
    Boolean existsByNameAndUser(String name, User user);
    boolean existsByNameAndUserAndIdNot(String name, User user, String id);
    Page<Category> findByUser(User user, Pageable pageable);
    Page<Category> findByUserId(Long userId, Pageable pageable);
}
