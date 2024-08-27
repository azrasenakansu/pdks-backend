package com.proven.pdks.repositories;

import com.proven.pdks.dtos.WorklogReportDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository  {
    List<WorklogReportDTO> getReport(LocalDate startDate,LocalDate endDate, List<String> tckns);

}
