package com.main.multithreaded_scanner_file.repo;

import com.main.multithreaded_scanner_file.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {

}
