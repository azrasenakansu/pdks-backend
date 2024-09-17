package com.proven.pdks.controllers;

import com.proven.pdks.services.EmailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailingService emailingService;

    @GetMapping("/{tckn}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> send(@PathVariable String tckn) {
        emailingService.sendReportMail(tckn, LocalDate.now());
        return ResponseEntity.ok().build();
    }

}
