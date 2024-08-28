package com.proven.pdks.controllers;

import com.proven.pdks.dtos.WorklogReportDTO;
import com.proven.pdks.entities.Worklog;
import com.proven.pdks.services.PDKSExportService;
import com.proven.pdks.services.WorklogService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/worklogs")
public class WorklogController {
    @Autowired
    private WorklogService worklogService;
    @Autowired
    private PDKSExportService excelExportService;

    @GetMapping()
    public List<Worklog> getWorklogs() {
        return worklogService.getWorklogs(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
    }

    @GetMapping("/{tckn}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Worklog> getWorklog(@PathVariable String tckn) {
        return worklogService.getWorklogs(tckn);
    }

    @GetMapping("/report")
    public List<WorklogReportDTO> getWorklogReportByTckn(@RequestParam LocalDate startDate,
                                                         @RequestParam LocalDate endDate,@RequestParam(required = false) List<String> tckns) {
        if(tckns == null){
            tckns = new ArrayList<>();
        }
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(q -> q.getAuthority().equals("ADMIN"));
        if(!isAdmin){
            tckns = List.of(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        }
        return worklogService.getReport(startDate,endDate,tckns);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/report/export")
    public void exportWorklogReport(@RequestParam LocalDate startDate,
                                    @RequestParam LocalDate endDate,
                                    @RequestParam(required = false) List<String> tckns,
                                    HttpServletResponse response) throws IOException {
        List<WorklogReportDTO> reports = worklogService.getReport(startDate, endDate, tckns);
        excelExportService.exportWorklogReport(reports, response);
    }
}
