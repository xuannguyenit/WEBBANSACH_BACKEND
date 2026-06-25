package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    Page<Banner> findByActiveTrueOrderBySortOrderAsc(Pageable pageable);

    boolean existsByTitle(String title);
}
