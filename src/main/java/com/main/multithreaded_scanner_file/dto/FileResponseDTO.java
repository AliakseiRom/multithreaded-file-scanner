package com.main.multithreaded_scanner_file.dto;

import com.main.multithreaded_scanner_file.model.FileEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FileResponseDTO {
    private String path;
    private List<FileEntity> files;
}
