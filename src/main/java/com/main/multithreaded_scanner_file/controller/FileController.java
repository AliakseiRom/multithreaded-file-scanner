package com.main.multithreaded_scanner_file.controller;

import com.main.multithreaded_scanner_file.dto.FileRequestDTO;
import com.main.multithreaded_scanner_file.dto.FileResponseDTO;
import com.main.multithreaded_scanner_file.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scan")
public class FileController {

    @Autowired
    private ScanService scanService;

    @GetMapping
    public ResponseEntity<FileResponseDTO> fileScan (@RequestBody FileRequestDTO fileRequestDTO) {
        return new ResponseEntity<>(scanService.scan(
                fileRequestDTO.path(),
                fileRequestDTO.mask()),
                HttpStatus.OK
        );
    }
}