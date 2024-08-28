package com.proven.pdks.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private LocalTime total_time;

    public LocalTime getTotal_time(){
       LocalTime totalTime = LocalTime.of(0,0);
        if(start_time != null && end_time != null){
            long minutes = ChronoUnit.MINUTES.between(start_time, end_time);
            totalTime=totalTime.plusMinutes(minutes);
        }
        if(ext_hours != null){
            totalTime = totalTime.plusMinutes(ext_hours.getMinute()).plusHours(ext_hours.getHour());
        }
        this.total_time = totalTime;
         return totalTime;
    }
}
