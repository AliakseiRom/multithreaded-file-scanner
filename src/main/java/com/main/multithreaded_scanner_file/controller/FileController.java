package com.main.multithreaded_scanner_file.controller;

import com.main.multithreaded_scanner_file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @DeleteMapping
    public ResponseEntity<String> deleteAllFiles() {
        fileService.deleteAllFiles();
        return ResponseEntity.ok("Successfully deleted all files");
    }
}
