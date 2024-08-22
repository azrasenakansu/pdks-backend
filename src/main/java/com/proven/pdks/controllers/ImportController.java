package com.proven.pdks.controllers;

import com.proven.pdks.common.SimpleRows;
import com.proven.pdks.parsers.PDKSExcelParser;
import com.proven.pdks.parsers.PDKSParser;
import com.proven.pdks.services.FileService;
import com.proven.pdks.services.PDKSImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private FileService fileService;

    @Autowired
    private PDKSParser pdksParser;

    @Autowired
    private PDKSImportService pdksImportService;

    @PostMapping()
    public ResponseEntity<Void> uploadFile(@RequestParam(name = "file") MultipartFile file){
        Path path = fileService.upload(file);
        if(path == null){
            return ResponseEntity.internalServerError().build();
        }
        if(!pdksParser.isFileSupported(StringUtils.getFilenameExtension(path.toString()))){
            return ResponseEntity.badRequest().build();
        }
        try{
            List<SimpleRows> rows = pdksParser.parse(path.toString());
            pdksImportService.importPDKS(rows);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



}
