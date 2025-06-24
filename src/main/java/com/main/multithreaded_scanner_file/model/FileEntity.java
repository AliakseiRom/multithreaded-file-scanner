package com.main.multithreaded_scanner_file.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "file")
@Data
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "path")
    private String path;

    @Column(name = "file_name")
    private String fileName;
}
