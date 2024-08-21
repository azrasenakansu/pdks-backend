package com.proven.pdks.parsers;

import com.proven.pdks.common.SimpleRows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface PDKSParser {
    boolean isFileSupported(String fileExtension);
    List<SimpleRows> parse(String filePath) throws IOException;
}
