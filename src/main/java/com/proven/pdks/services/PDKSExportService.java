package com.proven.pdks.services;

import com.proven.pdks.dtos.WorklogReportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PDKSExportService {

    public byte[] exportWorklogReport(List<WorklogReportDTO> reports) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Worklog Reports");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"TC Kimlik Numarası", "Adı Soyadı", "Tarih", "Başlangıç - Bitiş Saati", "Ek Çalışma Saati", "Açıklama", "Toplam Çalışma Saati"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(getHeaderStyle(workbook));
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (int i = 0; i < reports.size(); i++) {
            Row row = sheet.createRow(i + 1);
            WorklogReportDTO report = reports.get(i);

            row.createCell(0).setCellValue(report.getTckn());
            row.createCell(1).setCellValue(report.getName());
            row.createCell(2).setCellValue(report.getDate().format(dateFormatter));
            row.createCell(3).setCellValue((report.getStart_time() != null ? report.getStart_time() : "?") + " - " + (report.getEnd_time() != null ? report.getEnd_time() : "?"));
            LocalTime extHours = report.getExt_hours();
            if (extHours != null) {
                String extHoursFormatted = extHours.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                row.createCell(4).setCellValue(extHoursFormatted);
            } else {
                row.createCell(4).setCellValue("");
            }
            row.createCell(5).setCellValue(report.getExt_descriptions());
            if (report.getTotal_time() != null) {
                String totalHours = report.getTotal_time().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                row.createCell(6).setCellValue(totalHours);
            } else {
                row.createCell(6).setCellValue("");
            }

        }
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        byte[] result = null;
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            workbook.write(stream);
            workbook.close();
            result = stream.toByteArray();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
