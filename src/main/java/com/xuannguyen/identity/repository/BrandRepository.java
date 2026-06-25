package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand,String> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, String id);
    Optional<Brand> findByName(String name);
}
