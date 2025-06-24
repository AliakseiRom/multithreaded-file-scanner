package com.main.multithreaded_scanner_file.service;

import com.main.multithreaded_scanner_file.model.FileEntity;
import com.main.multithreaded_scanner_file.repo.FileEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FileService {

    @Autowired
    private FileEntityRepository fileRepository;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void scan(String path, String mask) {
        Path dir = Paths.get(path);
        CompletableFuture<Void> future = scanRecursive(dir, mask);
        future.thenRun(() -> System.out.println("Scanning is done"));
    }

    public CompletableFuture<Void> scanRecursive(Path dir, String mask) {
        return CompletableFuture.runAsync(() -> {
            ArrayList<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursive(entry, mask));
                    } else {
                        if (matchesMask(String.valueOf(entry.getFileName()), mask)) {
                            FileEntity fileEntity = new FileEntity();
                            fileEntity.setFileName(entry.getFileName().toString());
                            fileEntity.setPath(entry.toString());
                            fileRepository.save(fileEntity);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while scanning files", e);
            }

            CompletableFuture.allOf(subTasks.toArray(new CompletableFuture[0])).join();

        }, executor);
    }

    public boolean matchesMask(String fileName, String mask) {
        String regex = mask.replace(".", "\\.").replace("*", ".*");
        return fileName.matches(regex);
    }
}
