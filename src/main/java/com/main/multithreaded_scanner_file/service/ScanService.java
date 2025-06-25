package com.main.multithreaded_scanner_file.service;

import com.main.multithreaded_scanner_file.dto.FileResponseDTO;
import com.main.multithreaded_scanner_file.mapper.FileMapper;
import com.main.multithreaded_scanner_file.model.FileEntity;
import com.main.multithreaded_scanner_file.repo.FileEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ScanService {

    @Autowired
    private FileEntityRepository fileRepository;

    @Autowired
    private FileMapper fileMapper;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public FileResponseDTO scan(String path, String mask) {
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> future = scanRecursive(Path.of(path), mask, currentScanResults);
        future.join();

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    private CompletableFuture<Void> scanRecursive(Path dir, String mask, List<FileEntity> currentScanResults) {
        return CompletableFuture.runAsync(() -> {
            ArrayList<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursive(entry, mask, currentScanResults));
                    } else {
                        if (matchesMask(String.valueOf(entry.getFileName()), mask)) {
                            FileEntity fileEntity = new FileEntity();
                            fileEntity.setFileName(entry.getFileName().toString());
                            fileEntity.setPath(entry.toString());

                            fileEntity = fileRepository.save(fileEntity);
                            currentScanResults.add(fileEntity);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while scanning files", e);
            }

            CompletableFuture.allOf(subTasks.toArray(new CompletableFuture[0])).join();

        }, executor);
    }

    private boolean matchesMask(String fileName, String mask) {
        String regex = mask.replace(".", "\\.").replace("*", ".*");
        return fileName.matches(regex);
    }
}
