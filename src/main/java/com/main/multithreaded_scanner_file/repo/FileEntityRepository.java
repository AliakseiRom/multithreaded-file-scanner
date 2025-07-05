package com.main.multithreaded_scanner_file.repo;

import com.main.multithreaded_scanner_file.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, Integer> {
    Boolean existsByPath(String path);

    FileEntity findByPath(String path);

    @Modifying
    @Query("DELETE FROM file")
    void deleteAllInBulk();
}
