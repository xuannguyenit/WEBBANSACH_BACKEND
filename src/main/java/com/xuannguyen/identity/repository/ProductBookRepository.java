package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.ProductBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductBookRepository extends JpaRepository<ProductBook,String> {
    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    boolean existsByIsbn(String isbn);

    Optional<ProductBook> findBySlug(String slug);

    @Query("""
        select count(b) > 0
        from ProductBook b
        where lower(b.name) = lower(:name)
          and lower(b.bookAuthor) = lower(:bookAuthor)
          and (
                (:volumeNumber is null and b.volumeNumber is null)
                or b.volumeNumber = :volumeNumber
          )
          and b.user.id = :userId
    """)
    boolean existsDuplicateBook(
            @Param("name") String name,
            @Param("bookAuthor") String bookAuthor,
            @Param("volumeNumber") Integer volumeNumber,
            @Param("userId") Long userId
    );
}
