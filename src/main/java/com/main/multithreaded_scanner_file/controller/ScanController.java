package com.main.multithreaded_scanner_file.controller;

import com.main.multithreaded_scanner_file.dto.FileKBRequestDTO;
import com.main.multithreaded_scanner_file.dto.FileRequestDTO;
import com.main.multithreaded_scanner_file.dto.FileResponseDTO;
import com.main.multithreaded_scanner_file.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scan")
public class ScanController {

    @Autowired
    private ScanService scanService;

    @GetMapping
    public ResponseEntity<FileResponseDTO> fileScanMask (@RequestBody FileRequestDTO fileRequestDTO) {
        return new ResponseEntity<>(scanService.scan(
                fileRequestDTO.path(),
                fileRequestDTO.mask()),
                HttpStatus.OK
        );
    }

    @GetMapping("/kb")
    public ResponseEntity<FileResponseDTO> fileScanKB(@RequestBody FileKBRequestDTO fileRequestDTO) {
        return new ResponseEntity<>(scanService.scan(
                fileRequestDTO.path(),
                fileRequestDTO.kb()
        ), HttpStatus.OK);
    }
}