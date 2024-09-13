package com.proven.pdks.models;


import com.proven.pdks.dtos.WorklogReportDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailModel {
    private String receiver;
    private String subject;
    private String name;
    private List<WorklogReportDTO> worklogs;
    private Duration totalTime;

    public void calculateTotal(){
        totalTime = worklogs.stream().map(WorklogReportDTO::getTotal_time).reduce(Duration.ofHours(0), (accDuration,x) -> {
            accDuration = accDuration.plusHours(x.getHour());
            accDuration = accDuration.plusMinutes(x.getMinute());
            return accDuration;
        }, (Duration::plus));
    }
}
