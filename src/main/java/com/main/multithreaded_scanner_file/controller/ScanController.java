package com.main.multithreaded_scanner_file.controller;

import com.main.multithreaded_scanner_file.dto.*;
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

    @GetMapping("/kb/mask")
    public ResponseEntity<FileResponseDTO> fileScanKBAndMask (@RequestBody FileKBAndMaskRequestDTO fileKBAndMaskRequestDTO) {
        return new ResponseEntity<>(scanService.scan(
                fileKBAndMaskRequestDTO.path(),
                fileKBAndMaskRequestDTO.mask(),
                fileKBAndMaskRequestDTO.kb()
        ), HttpStatus.OK);
    }

    @GetMapping("/time")
    public ResponseEntity<FileResponseDTO> fileScanTime (@RequestBody FileDataRequestDTO fileDataRequestDTO) {
        return new ResponseEntity<>(scanService.scanTime(
                fileDataRequestDTO.path(),
                fileDataRequestDTO.time()
        ), HttpStatus.OK);
    }

    @GetMapping("/content")
    public ResponseEntity<FileResponseDTO> fileScanContent (@RequestBody FileContentRequestDTO fileContentRequestDTO) {
        return new ResponseEntity<>(scanService.scanContent(
                fileContentRequestDTO.path(),
                fileContentRequestDTO.content()
        ), HttpStatus.OK);
    }
}