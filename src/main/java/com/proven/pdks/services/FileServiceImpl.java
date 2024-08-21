package com.proven.pdks.services;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final String basePath = "resources/";

    private void initDirectory(){
        try {
            Files.createDirectories(Paths.get("resources/"));
        } catch (IOException e) {
            // nothing to do
        }
    }

    @Override
    public Path upload(MultipartFile file) {
        initDirectory();
        String extension = "." + StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + extension;
        if(new File(basePath + fileName).exists()){
            return null;
        }
        Path path = Path.of(basePath+fileName);
        try{
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return path;
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}
