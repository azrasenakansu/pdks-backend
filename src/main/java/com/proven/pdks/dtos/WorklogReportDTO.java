package com.proven.pdks.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorklogReportDTO {
    private String name;
    private String tckn;
    private LocalDate date;
    private LocalTime start_time;
    private LocalTime end_time;
    private LocalTime ext_hours;
    private String ext_descriptions;
}
