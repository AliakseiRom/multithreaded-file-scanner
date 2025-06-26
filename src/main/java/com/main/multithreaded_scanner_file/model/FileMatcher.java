package com.main.multithreaded_scanner_file.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;

public class FileMatcher {

    //By mask
    public static boolean matchesMask(String fileName, String mask) {
        String regex = mask.replace(".", "\\.").replace("*", ".*");
        return fileName.matches(regex);
    }

    //By kb borders
    public static boolean matchesKBBorders(Path dir, Long[] kb) throws IOException {
        Long sizeInKB = Files.size(dir) / 1024;
        return sizeInKB <= kb[1] && sizeInKB >= kb[0];
    }

    //By time
    public static boolean matchesTime(Path dir, String time) throws IOException {
        Long compareTime = TimeParser.parseTimeToMillis(time);

        FileTime lastModified = Files.getLastModifiedTime(dir);
        Instant lastModifiedInstant = lastModified.toInstant();
        Instant nowInstant = Instant.now();
        Long fileLastModified = Duration.between(lastModifiedInstant, nowInstant).toMillis();

        System.out.println("Прошло с момента изменения (мс): " + fileLastModified);
        System.out.println("Сравнение с интервалом (мс): " + compareTime);

        return fileLastModified <= compareTime;
    }
}
