package com.main.multithreaded_scanner_file.dto;

import com.main.multithreaded_scanner_file.model.FileEntity;
import lombok.Builder;

import java.util.List;

@Builder(setterPrefix = "set", toBuilder = true)
public record FileResponseDTO(
        String path,
        List<FileEntity> files
) {
}
