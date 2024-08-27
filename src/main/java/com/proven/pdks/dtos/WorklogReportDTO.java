package com.proven.pdks.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

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

    public LocalTime getTotalTime(){
        LocalTime totalTime = LocalTime.of(0,0);
        if(start_time != null && end_time != null){
            long hours = ChronoUnit.HOURS.between(start_time, end_time);
            long minutes = ChronoUnit.MINUTES.between(start_time, end_time);
            totalTime = LocalTime.of((int) hours, (int) minutes);
        }
        if(ext_hours != null){
            totalTime = totalTime.plusMinutes(ext_hours.getMinute()).plusHours(ext_hours.getHour());
        }
        return  totalTime;
    }
}
