package com.proven.pdks.services;

import com.proven.pdks.dtos.WorklogReportDTO;
import com.proven.pdks.entities.Worklog;
import com.proven.pdks.repositories.ReportRepository;
import com.proven.pdks.repositories.WorklogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorklogServiceImpl implements WorklogService {
    private final WorklogRepository worklogRepository;
    private final ReportRepository reportRepository;

    @Autowired
    public WorklogServiceImpl(WorklogRepository worklogRepository, ReportRepository reportRepository) {
        this.worklogRepository = worklogRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public List<Worklog> getWorklogs(String tckn) {
        return worklogRepository.findByUser_Tckn(tckn);
    }

    @Override
    public List<WorklogReportDTO> getReport(LocalDate startDate, LocalDate endDate, List<String> tckns) {
        List<WorklogReportDTO> reports =reportRepository.getReport(startDate, endDate, tckns);
        for (WorklogReportDTO reportDTO:reports){
            reportDTO.getTotal_time();
        }
        return reports;
    }
}
