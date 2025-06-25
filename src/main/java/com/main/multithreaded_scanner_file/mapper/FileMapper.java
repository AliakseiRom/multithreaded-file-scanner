package com.main.multithreaded_scanner_file.mapper;

import com.main.multithreaded_scanner_file.dto.FileResponseDTO;
import com.main.multithreaded_scanner_file.model.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target = "path", source = "path")
    @Mapping(target = "files", source = "files")
    FileResponseDTO toResponseDTO(String path, List<FileEntity> files);
}
