package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.ProductArtwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductArtworkRepository extends JpaRepository<ProductArtwork, String> {

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM ProductArtwork p
        WHERE LOWER(p.name) = LOWER(:name)
          AND LOWER(p.artistName) = LOWER(:artistName)
          AND p.user.id = :userId
    """)
    boolean existsDuplicateArtwork(
            String name,
            String artistName,
            Long userId
    );
}