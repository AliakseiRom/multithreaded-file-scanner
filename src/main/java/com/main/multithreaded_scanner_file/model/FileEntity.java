package com.main.multithreaded_scanner_file.model;

import jakarta.persistence.*;

@Entity(name = "file")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "path")
    private String path;

    @Column(name = "file_name")
    private String fileName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
