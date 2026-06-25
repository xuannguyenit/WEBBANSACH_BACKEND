package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,String> {
    Optional<Cart> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
