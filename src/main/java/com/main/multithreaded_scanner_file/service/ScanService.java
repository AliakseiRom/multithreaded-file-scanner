package com.main.multithreaded_scanner_file.service;

import com.main.multithreaded_scanner_file.dto.FileResponseDTO;
import com.main.multithreaded_scanner_file.mapper.FileMapper;
import com.main.multithreaded_scanner_file.model.FileEntity;
import com.main.multithreaded_scanner_file.model.FileMatcher;
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

    private final ExecutorService executor = Executors.newFixedThreadPool(50);

    //By mask
    public FileResponseDTO scan(String path, String mask) {
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> future = scanRecursive(Path.of(path), mask, currentScanResults);
        future.join();

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    //By KB
    public FileResponseDTO scan(String path, Long[] kb) {
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> future = scanRecursive(Path.of(path), kb, currentScanResults);
        future.join();

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    //By KB and mask
    public FileResponseDTO scan(String path, String mask, Long[] kb) {
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> future = scanRecursive(Path.of(path), mask, kb, currentScanResults);
        future.join();

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    //By time
    public FileResponseDTO scanTime(String path, String time) {
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> future = scanRecursiveByTime(Path.of(path), time, currentScanResults);
        future.join();

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    /// //////////////////////////////////////////////////////////////////////////

    //By KB
    private CompletableFuture<Void> scanRecursive(Path dir, Long[] kb, List<FileEntity> currentScanResults) {
        return CompletableFuture.runAsync(() -> {
            ArrayList<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursive(entry, kb, currentScanResults));
                    } else {
                        if (FileMatcher.matchesKBBorders(entry, kb)) {
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

    /// ////////////////////////////////////////////////////////////////////////////////

    //By mask
    private CompletableFuture<Void> scanRecursive(Path dir, String mask, List<FileEntity> currentScanResults) {
        return CompletableFuture.runAsync(() -> {
            ArrayList<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursive(entry, mask, currentScanResults));
                    } else {
                        if (FileMatcher.matchesMask(String.valueOf(entry.getFileName()), mask)) {
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

    //////////////////////////////////////////////////////////////////////////////////////

    //By KB and mask
    private CompletableFuture<Void> scanRecursive(Path dir, String mask, Long[] kb, List<FileEntity> currentScanResults) {
        return CompletableFuture.runAsync(() -> {
            ArrayList<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursive(entry, mask, kb, currentScanResults));
                    } else {
                        if (FileMatcher.matchesMask(String.valueOf(entry.getFileName()), mask)) {
                            if (FileMatcher.matchesKBBorders(entry, kb)) {
                                FileEntity fileEntity = new FileEntity();
                                fileEntity.setFileName(entry.getFileName().toString());
                                fileEntity.setPath(entry.toString());

                                fileEntity = fileRepository.save(fileEntity);
                                currentScanResults.add(fileEntity);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while scanning files", e);
            }

            CompletableFuture.allOf(subTasks.toArray(new CompletableFuture[0])).join();

        }, executor);
    }

    /// /////////////////////////////////////////////////////////////////////////////////////

    //By time
    private CompletableFuture<Void> scanRecursiveByTime(Path dir, String time, List<FileEntity> currentScanResults) {
        return CompletableFuture.runAsync(() -> {
            ArrayList<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursiveByTime(entry, time, currentScanResults));
                    } else {
                        if (FileMatcher.matchesTime(entry, time)) {
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
}
