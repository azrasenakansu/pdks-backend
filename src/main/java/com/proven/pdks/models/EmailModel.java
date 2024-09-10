package com.proven.pdks.models;


import com.proven.pdks.dtos.WorklogReportDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailModel {
    private String receiver;
    private String subject;
    private String name;
    private List<WorklogReportDTO> worklogs;
}
