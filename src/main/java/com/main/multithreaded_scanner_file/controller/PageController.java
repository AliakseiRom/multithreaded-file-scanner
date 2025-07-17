package com.main.multithreaded_scanner_file.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/mask")
    public String mask() {
        return "mask";
    }

    @GetMapping("/kb")
    public String kb() {
        return "kb";
    }

    @GetMapping("/content")
    public String content() {
        return "content";
    }

    @GetMapping("/kbMask")
    public String kbAndMask() {
        return "kbAndMask";
    }

    @GetMapping("/time")
    public String time() {
        return "time";
    }
}
