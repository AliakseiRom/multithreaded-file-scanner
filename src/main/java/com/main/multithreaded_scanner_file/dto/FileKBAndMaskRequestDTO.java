package com.main.multithreaded_scanner_file.dto;

import lombok.Builder;

@Builder(setterPrefix = "set", toBuilder = true)
public record FileKBAndMaskRequestDTO(
        String path,
        String mask,
        Long[] kb
) {
}


