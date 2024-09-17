package com.proven.pdks.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proven.pdks.helpers.FormatterHelper;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime start_time;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime end_time;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="HH:mm")
    private LocalTime ext_hours;

    private String ext_descriptions;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="HH:mm")
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

    @JsonIgnore
    public String getDateText(){
        return FormatterHelper.dateFormatter.format(date);
    }

    public String getUniqueName(){
        return this.name + "_" + this.tckn;
    }
}
