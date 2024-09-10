package com.proven.pdks.controllers;

import com.proven.pdks.services.EmailingService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class EmailController {
    private final EmailingService emailingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void sendMail(@RequestParam("tckn")  String tckn) throws MessagingException {
        emailingService.sendWeeklyMail(tckn);
    }
}