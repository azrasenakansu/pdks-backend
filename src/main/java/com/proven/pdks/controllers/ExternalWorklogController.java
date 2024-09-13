package com.proven.pdks.controllers;

import com.proven.pdks.entities.ExternalWorklog;
import com.proven.pdks.entities.User;
import com.proven.pdks.services.ExternalWorklogService;
import com.proven.pdks.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/externalWorklogs")
public class ExternalWorklogController {

    private final ExternalWorklogService externalWorklogService;
    private final UserService userService;

    @Autowired
    public ExternalWorklogController(ExternalWorklogService externalWorklogService, UserService userService) {
        this.externalWorklogService = externalWorklogService;
        this.userService = userService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public void create(@RequestBody ExternalWorklog externalWorklog) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User currentUser = userService.findByTCKN(username);
        externalWorklog.setUser(currentUser);
        externalWorklogService.createWorklog(externalWorklog);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public Page<ExternalWorklog> getExternalWorklogs(@RequestParam(name = "page",defaultValue = "0") int page, @RequestParam(name = "size",defaultValue = "10") int size) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return externalWorklogService.getWorklogs(username, page, size);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ExternalWorklog updateWorklog(@PathVariable Long id, @RequestBody ExternalWorklog worklogDetails) {
        return externalWorklogService.updateWorklog(id, worklogDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteWorklog(@PathVariable Long id) {
        externalWorklogService.deleteWorklog(id);
    }

    @PatchMapping("/approve/{id}/{state}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ExternalWorklog approveWorklog(@PathVariable Long id, @PathVariable Integer state) {
        Optional<Boolean> approveState = state == 0 ? Optional.of(false) : state == 1 ? Optional.of(true) : Optional.empty();
        return externalWorklogService.approveWorklog(id, approveState);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<ExternalWorklog> getAllPendingWorklogs(@RequestParam(name = "page",defaultValue = "0") int page, @RequestParam(name = "size",defaultValue = "10") int size) {
        return externalWorklogService.getAllPendingWorklogs(page, size);
    }

    @GetMapping("/rejected")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<ExternalWorklog> getAllRejectedWorklogs(@RequestParam(name = "page",defaultValue = "0") int page, @RequestParam(name = "size",defaultValue = "10") int size) {
        return externalWorklogService.getAllRejectedWorklogs(page,size);
    }

    @GetMapping("/approved")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<ExternalWorklog> getAllApprovedWorklogs(@RequestParam(name = "page",defaultValue = "0") int page, @RequestParam(name = "size",defaultValue = "10") int size) {
        return externalWorklogService.getAllApprovedWorklogs(page,size);
    }
}
