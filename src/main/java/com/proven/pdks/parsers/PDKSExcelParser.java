package com.proven.pdks.parsers;

import com.proven.pdks.common.SimpleRows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PDKSExcelParser implements PDKSParser {

    @Override
    public boolean isFileSupported(String fileExtension) {
        return "xlsx".equals(fileExtension) || "xls".equals(fileExtension);
    }

    @Override
    public List<SimpleRows> parse(String filePath) throws IOException {
        FileInputStream file = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d.M.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        List<SimpleRows> simpleRowList = new ArrayList<>();

        for (int i = 6; i < sheet.getLastRowNum() + 1; i++) {
            Row row = sheet.getRow(i);

            Long id = (long) row.getCell(0).getNumericCellValue();
            Long sicil = (long) row.getCell(1).getNumericCellValue();
            String name = row.getCell(2).getStringCellValue();

            Cell dateCell = row.getCell(3);
            Cell timeCell = row.getCell(4);
            String status = row.getCell(5).getStringCellValue();
            String source = row.getCell(6).getStringCellValue();

            LocalDate localDate;
            LocalTime localTime;
            if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                Date date = dateCell.getDateCellValue();
                localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
                localTime = LocalTime.parse(timeCell.getStringCellValue(), timeFormatter);
            } else {
                String date = dateCell.getStringCellValue();
                String time = timeCell.getStringCellValue();
                localDate = LocalDate.parse(date, dateFormatter);
                localTime = LocalTime.parse(time, timeFormatter);
            }

            SimpleRows simpleRowInformations = new SimpleRows();
            simpleRowInformations.setTckn(String.valueOf(id));
            simpleRowInformations.setSicil(sicil);
            simpleRowInformations.setName(name);
            simpleRowInformations.setDate(localDate);
            simpleRowInformations.setTime(localTime);
            simpleRowInformations.setStatus(status);
            simpleRowInformations.setSource(source);

            simpleRowList.add(simpleRowInformations);
        }

        file.close();
        workbook.close();
        return simpleRowList;
    }
}
