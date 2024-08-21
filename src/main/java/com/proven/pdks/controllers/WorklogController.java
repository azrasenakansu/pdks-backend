package com.proven.pdks.controllers;

import com.proven.pdks.entities.ExternalWorklog;
import com.proven.pdks.entities.Worklog;
import com.proven.pdks.services.WorklogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worklogs")
public class WorklogController {
    @Autowired
    private WorklogService worklogService;

    @GetMapping()
    public List<Worklog> getWorklogs() {
        return worklogService.getWorklogs(((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
    }

    @GetMapping("/{tckn}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Worklog> getWorklog(@PathVariable String tckn) {
        return worklogService.getWorklogs(tckn);
    }

}
