package com.main.multithreaded_scanner_file.controller;

import com.main.multithreaded_scanner_file.dto.FileResponseDTO;
import com.main.multithreaded_scanner_file.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class ThymeLeafController {

    @Autowired
    private ScanService scanService;

    @GetMapping("/scanth")
    public String FileScanMask(
            @RequestParam String path,
            @RequestParam String mask,
            Model model
    ) {
        if (path != null && mask != null) {
            FileResponseDTO result = scanService.scan(path, mask);
            model.addAttribute("result", result);
        }
        return "mask.html";
    }
}
