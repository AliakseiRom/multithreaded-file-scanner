package com.main.multithreaded_scanner_file.service;

import com.main.multithreaded_scanner_file.repo.FileEntityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    @Autowired
    private FileEntityRepository fileEntityRepository;

    @Transactional
    public void deleteAllFiles() {
        fileEntityRepository.deleteAllInBulk();
    }
}
