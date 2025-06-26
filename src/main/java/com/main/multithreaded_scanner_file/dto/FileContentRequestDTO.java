package com.main.multithreaded_scanner_file.dto;

import lombok.Builder;

@Builder(setterPrefix = "set", toBuilder = true)
public record FileContentRequestDTO(
        String path,
        String content
) {
}
