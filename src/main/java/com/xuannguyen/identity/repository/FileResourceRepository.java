package com.xuannguyen.identity.repository;

import com.xuannguyen.identity.entity.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileResourceRepository extends JpaRepository<FileResource,String> {
}
