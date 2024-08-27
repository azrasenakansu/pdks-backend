package com.proven.pdks.services;

import com.proven.pdks.dtos.WorklogReportDTO;
import com.proven.pdks.entities.Worklog;

import java.time.LocalDate;
import java.util.List;

public interface WorklogService {
    List<Worklog> getWorklogs(String tckn);
    List<WorklogReportDTO> getReport(LocalDate startDate, LocalDate endDate, List<String> tckns);
}
