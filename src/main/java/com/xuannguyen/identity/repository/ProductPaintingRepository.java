package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.ProductPainting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPaintingRepository extends JpaRepository<ProductPainting, String> {
    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    Optional<ProductPainting> findBySlug(String slug);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM ProductPainting p
        WHERE LOWER(p.name) = LOWER(:name)
          AND LOWER(p.artistName) = LOWER(:artistName)
          AND p.user.id = :userId
    """)
    boolean existsDuplicatePainting(
            String name,
            String artistName,
            Long userId
    );
}
