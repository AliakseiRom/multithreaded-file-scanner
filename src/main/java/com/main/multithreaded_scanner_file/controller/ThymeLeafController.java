package com.main.multithreaded_scanner_file.controller;

import com.main.multithreaded_scanner_file.dto.FileResponseDTO;
import com.main.multithreaded_scanner_file.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/scanthM")
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

    @GetMapping("/scanthKB")
    public String FileScanKB(
            @RequestParam String path,
            @RequestParam String kb,
            Model model
    ) {
        if (path != null && kb != null) {
            String[] borders = kb.split(",");
            Long[] bordersLong = new Long[borders.length];
            bordersLong[0] = Long.parseLong(borders[0].trim());
            bordersLong[1] = Long.parseLong(borders[1].trim());
            FileResponseDTO result = scanService.scan(path, bordersLong);
            model.addAttribute("result", result);
        }
        return "kb.html";
    }

    @GetMapping("/scanthContent")
    public String FileScanContent(
            @RequestParam String path,
            @RequestParam String content,
            Model model
    ) {
        if (path != null && content != null) {
            FileResponseDTO result = scanService.scanContent(path, content);
            model.addAttribute("result", result);
        }
        return "content.html";
    }

    @GetMapping("/scanthKBAndMask")
    public String FileScanKBAndMask(
            @RequestParam String path,
            @RequestParam String kb,
            @RequestParam String mask,
            Model model
    ) {
        if (path != null && kb != null && mask != null) {
            String[] borders = kb.split(",");
            Long[] bordersLong = new Long[borders.length];
            bordersLong[0] = Long.parseLong(borders[0].trim());
            bordersLong[1] = Long.parseLong(borders[1].trim());
            FileResponseDTO result = scanService.scan(path, mask, bordersLong);
            model.addAttribute("result", result);
        }
        return "kbAndMask.html";
    }

    @GetMapping("/scanthTime")
    public String FileScanTime(
            @RequestParam String path,
            @RequestParam String time,
            Model model
    ) {
        if (path != null && time != null) {
            FileResponseDTO result = scanService.scanTime(path, time);
            model.addAttribute("result", result);
        }
        return "time.html";
    }
}
