package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    boolean existsById(String id);
}
