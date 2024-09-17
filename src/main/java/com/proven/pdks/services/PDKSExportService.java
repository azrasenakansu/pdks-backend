package com.proven.pdks.services;

import com.proven.pdks.dtos.WorklogReportDTO;
import com.proven.pdks.helpers.FormatterHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class PDKSExportService {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] headers = {"TC Kimlik Numarası", "Adı Soyadı", "Tarih", "Başlangıç - Bitiş Saati", "Ek Çalışma Saati", "Açıklama", "Toplam Çalışma Saati"};

    private void createReportSheet(Workbook workbook, CellStyle mergedLabelStyle, CellStyle mergedValueStyle, List<WorklogReportDTO> userReport){
        if(userReport == null || userReport.isEmpty()){
            return;
        }
        Sheet sheet = workbook.createSheet(userReport.getFirst().getUniqueName());
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(mergedValueStyle);
        }

        Duration totalTime = userReport.stream().map(WorklogReportDTO::getTotal_time).reduce(Duration.ofHours(0), (accDuration, x) -> {
            accDuration = accDuration.plusHours(x.getHour());
            accDuration = accDuration.plusMinutes(x.getMinute());
            return accDuration;
        }, (Duration::plus));
        String totalTimeText = FormatterHelper.formatter.format(totalTime.toHours()) + ":" + FormatterHelper.formatter.format(totalTime.toMinutesPart());

        for (int i = 0; i < userReport.size(); i++) {
            Row row = sheet.createRow(i+1);
            WorklogReportDTO report = userReport.get(i);

            row.createCell(0).setCellValue(report.getTckn());
            row.createCell(1).setCellValue(report.getName());
            row.createCell(2).setCellValue(report.getDate().format(dateFormatter));
            row.createCell(3).setCellValue((report.getStart_time() != null ? report.getStart_time() : "") + " - " + (report.getEnd_time() != null ? report.getEnd_time() : ""));
            LocalTime extHours = report.getExt_hours();
            if (extHours != null) {
                String extHoursFormatted = extHours.format(DateTimeFormatter.ofPattern("HH:mm"));
                row.createCell(4).setCellValue(extHoursFormatted);
            } else {
                row.createCell(4).setCellValue("");
            }
            row.createCell(5).setCellValue(report.getExt_descriptions());
            if (report.getTotal_time() != null) {
                String totalHours = report.getTotal_time().format(DateTimeFormatter.ofPattern("HH:mm"));
                row.createCell(6).setCellValue(totalHours);
            } else {
                row.createCell(6).setCellValue("");
            }
        }

        int indexForTotalRow = 1 + userReport.size();
        Row row = sheet.createRow(indexForTotalRow);

        Cell labelTotalCell = row.createCell(0);
        labelTotalCell.setCellStyle(mergedLabelStyle);
        labelTotalCell.setCellValue("Toplam");

        Cell valueTootalCell = row.createCell(6);
        valueTootalCell.setCellStyle(mergedValueStyle);
        valueTootalCell.setCellValue(totalTimeText);

        sheet.addMergedRegion(new CellRangeAddress(indexForTotalRow, indexForTotalRow, 0,5));

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public byte[] exportWorklogReport(List<WorklogReportDTO> reports) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        CellStyle mergedLabelStyle = getHeaderStyle(workbook);
        mergedLabelStyle.setAlignment(HorizontalAlignment.RIGHT);
        CellStyle mergedValueStyle = getHeaderStyle(workbook);

        Map<String, List<WorklogReportDTO>> userGroups = reports.stream().collect(Collectors.groupingBy(WorklogReportDTO::getUniqueName, TreeMap::new, Collectors.toList()));
        for(List<WorklogReportDTO> data : userGroups.values()){
            createReportSheet(workbook, mergedLabelStyle, mergedValueStyle, data);
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
