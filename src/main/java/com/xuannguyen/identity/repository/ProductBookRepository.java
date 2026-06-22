package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.ProductBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductBookRepository extends JpaRepository<ProductBook,String> {
    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    boolean existsByIsbn(String isbn);

    Optional<ProductBook> findBySlug(String slug);
}
