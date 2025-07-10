package com.main.multithreaded_scanner_file.service;

import com.main.multithreaded_scanner_file.dto.FileResponseDTO;
import com.main.multithreaded_scanner_file.exception.ScanCancelledException;
import com.main.multithreaded_scanner_file.mapper.FileMapper;
import com.main.multithreaded_scanner_file.model.FileEntity;
import com.main.multithreaded_scanner_file.model.FileMatcher;
import com.main.multithreaded_scanner_file.repo.FileEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ScanService {

    @Autowired
    private FileEntityRepository fileRepository;

    @Autowired
    private FileMapper fileMapper;

    private final ExecutorService executor = Executors.newFixedThreadPool(20);

    private final AtomicBoolean cancelRequest = new AtomicBoolean(false);

    public void cancelRequest() {
        cancelRequest.set(true);
    }

    public void resetCancelRequest() {
        cancelRequest.set(false);
    }

    //By mask
    public FileResponseDTO scan(String path, String mask) {
        resetCancelRequest();
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> future = scanRecursive(Path.of(path), mask, currentScanResults);

        try {
            future.get(3, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            throw new RuntimeException("Scan timeout", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ScanCancelledException) {
                throw (ScanCancelledException) e.getCause();
            }
            throw new RuntimeException("Scan failed", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Scan interrupted", e);
        }

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    //By KB
    public FileResponseDTO scan(String path, Long[] kb) {
        resetCancelRequest();
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> future = scanRecursive(Path.of(path), kb, currentScanResults);

        try {
            future.get(3, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            throw new RuntimeException("Scan timeout", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ScanCancelledException) {
                throw (ScanCancelledException) e.getCause();
            }
            throw new RuntimeException("Scan failed", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Scan interrupted", e);
        }

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    //By KB and mask
    public FileResponseDTO scan(String path, String mask, Long[] kb) {
        resetCancelRequest();
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> future = scanRecursive(Path.of(path), mask, kb, currentScanResults);

        try {
            future.get(3, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            throw new RuntimeException("Scan timeout", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ScanCancelledException) {
                throw (ScanCancelledException) e.getCause();
            }
            throw new RuntimeException("Scan failed", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Scan interrupted", e);
        }

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    //By time
    public FileResponseDTO scanTime(String path, String time) {
        resetCancelRequest();
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> future = scanRecursiveByTime(Path.of(path), time, currentScanResults);

        try {
            future.get(3, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            throw new RuntimeException("Scan timeout", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ScanCancelledException) {
                throw (ScanCancelledException) e.getCause();
            }
            throw new RuntimeException("Scan failed", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Scan interrupted", e);
        }

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    // By content
    public FileResponseDTO scanContent(String path, String content) {
        resetCancelRequest();
        List<FileEntity> currentScanResults = Collections.synchronizedList(new ArrayList<>());

        String mask = "*.txt";

        CompletableFuture<Void> future = scanRecursive(Path.of(path), content, mask, currentScanResults);

        try {
            future.get(3, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            throw new RuntimeException("Scan timeout", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ScanCancelledException) {
                throw (ScanCancelledException) e.getCause();
            }
            throw new RuntimeException("Scan failed", e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Scan interrupted", e);
        }

        return fileMapper.toResponseDTO(path, currentScanResults);
    }

    /// /////////////////////////////////////////////////////////////////////////////

    //By KB
    private CompletableFuture<Void> scanRecursive(Path dir, Long[] kb, List<FileEntity> currentScanResults) {
        return CompletableFuture.supplyAsync(() -> {
            if (cancelRequest.get()) {
                throw new ScanCancelledException("Scan cancelled");
            }

            List<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (cancelRequest.get()) {
                        throw new ScanCancelledException("Scan cancelled");
                    }

                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursive(entry, kb, currentScanResults));
                    } else {
                        try {
                            if (FileMatcher.matchesKBBorders(entry, kb)) {
                                if (!fileRepository.existsByPath(String.valueOf(entry))) {
                                    FileEntity fileEntity = new FileEntity();
                                    fileEntity.setFileName(entry.getFileName().toString());
                                    fileEntity.setPath(entry.toString());

                                    fileEntity = fileRepository.save(fileEntity);
                                    currentScanResults.add(fileEntity);
                                } else {
                                    FileEntity fileEntity = fileRepository.findByPath(String.valueOf(entry));
                                    currentScanResults.add(fileEntity);
                                    System.out.println("Took file from database");
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Cannot access file size for: " + entry + " - " + e.getMessage());
                        }
                    }
                }
            } catch (AccessDeniedException e) {
                System.out.println("Access denied in directory: " + dir);
            } catch (IOException e) {
                throw new RuntimeException("Error while scanning " + dir, e);
            }

            return subTasks;
        }, executor).thenCompose(tasks ->
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
        );
    }

    // By mask
    private CompletableFuture<Void> scanRecursive(Path dir, String mask, List<FileEntity> currentScanResults) {
        return CompletableFuture.supplyAsync(() -> {
            if (cancelRequest.get()) {
                throw new ScanCancelledException("Scan cancelled");
            }

            List<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (cancelRequest.get()) {
                        throw new ScanCancelledException("Scan cancelled");
                    }

                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursive(entry, mask, currentScanResults));
                    } else {
                        if (FileMatcher.matchesMask(entry.getFileName().toString(), mask)) {
                            processFile(entry, currentScanResults);
                        }
                    }
                }
            } catch (AccessDeniedException e) {
                System.out.println("Access denied in directory: " + dir);
            } catch (IOException e) {
                throw new RuntimeException("Error while scanning " + dir, e);
            }

            return subTasks;
        }, executor).thenCompose(tasks ->
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
        );
    }

    // By KB and mask
    private CompletableFuture<Void> scanRecursive(Path dir, String mask, Long[] kb, List<FileEntity> currentScanResults) {
        return CompletableFuture.supplyAsync(() -> {
            if (cancelRequest.get()) {
                throw new ScanCancelledException("Scan cancelled");
            }

            List<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (cancelRequest.get()) {
                        throw new ScanCancelledException("Scan cancelled");
                    }

                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursive(entry, mask, kb, currentScanResults));
                    } else {
                        if (FileMatcher.matchesMask(entry.getFileName().toString(), mask) &&
                                FileMatcher.matchesKBBorders(entry, kb)) {
                            processFile(entry, currentScanResults);
                        }
                    }
                }
            } catch (AccessDeniedException e) {
                System.out.println("Access denied in directory: " + dir);
            } catch (IOException e) {
                throw new RuntimeException("Error while scanning " + dir, e);
            }

            return subTasks;
        }, executor).thenCompose(tasks ->
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
        );
    }

    // By time
    private CompletableFuture<Void> scanRecursiveByTime(Path dir, String time, List<FileEntity> currentScanResults) {
        return CompletableFuture.supplyAsync(() -> {
            if (cancelRequest.get()) {
                throw new ScanCancelledException("Scan cancelled");
            }

            List<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (cancelRequest.get()) {
                        throw new ScanCancelledException("Scan cancelled");
                    }

                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursiveByTime(entry, time, currentScanResults));
                    } else {
                        if (FileMatcher.matchesTime(entry, time)) {
                            processFile(entry, currentScanResults);
                        }
                    }
                }
            } catch (AccessDeniedException e) {
                System.out.println("Access denied in directory: " + dir);
            } catch (IOException e) {
                throw new RuntimeException("Error while scanning " + dir, e);
            }

            return subTasks;
        }, executor).thenCompose(tasks ->
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
        );
    }

    // By content
    private CompletableFuture<Void> scanRecursive(Path dir, String content, String mask, List<FileEntity> currentScanResults) {
        return CompletableFuture.supplyAsync(() -> {
            if (cancelRequest.get()) {
                throw new ScanCancelledException("Scan cancelled");
            }

            List<CompletableFuture<Void>> subTasks = new ArrayList<>();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    if (cancelRequest.get()) {
                        throw new ScanCancelledException("Scan cancelled");
                    }

                    if (Files.isDirectory(entry)) {
                        subTasks.add(scanRecursive(entry, content, mask, currentScanResults));
                    } else {
                        if (FileMatcher.matchesMask(entry.getFileName().toString(), mask) &&
                                FileMatcher.matchesContent(entry, content)) {
                            processFile(entry, currentScanResults);
                        }
                    }
                }
            } catch (AccessDeniedException e) {
                System.out.println("Access denied in directory: " + dir);
            } catch (IOException e) {
                throw new RuntimeException("Error while scanning " + dir, e);
            }

            return subTasks;
        }, executor).thenCompose(tasks ->
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]))
        );
    }

    private void processFile(Path entry, List<FileEntity> currentScanResults) {
        String pathStr = entry.toString();
        if (!fileRepository.existsByPath(pathStr)) {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(entry.getFileName().toString());
            fileEntity.setPath(pathStr);

            fileEntity = fileRepository.save(fileEntity);
            currentScanResults.add(fileEntity);
        } else {
            FileEntity fileEntity = fileRepository.findByPath(pathStr);
            currentScanResults.add(fileEntity);
            System.out.println("Took file from database");
        }
    }
}