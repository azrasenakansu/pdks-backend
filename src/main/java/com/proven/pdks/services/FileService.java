package com.proven.pdks.services;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileService {
    Path upload(MultipartFile file);
    ResponseEntity<byte[]> download(byte[] data, String filename);
}
