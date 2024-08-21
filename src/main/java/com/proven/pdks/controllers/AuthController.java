package com.proven.pdks.controllers;

import com.proven.pdks.dtos.ChangePasswordDto;
import com.proven.pdks.dtos.LoginDto;
import com.proven.pdks.dtos.LoginResponseDto;
import com.proven.pdks.services.AuthanticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthanticationService authanticationService;

    @Autowired
    public AuthController(AuthanticationService authanticationService) {
        this.authanticationService = authanticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(authanticationService.login(dto));
    }

    @PutMapping("/resetPassword")
    @PreAuthorize("hasAuthority('ADMIN')") // Only admin can reset passwords
    public ResponseEntity<Void> resetPassword(@RequestParam String tckn) {
        authanticationService.resetPassword(tckn);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/changePassword")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDto dto) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        authanticationService.changePassword(username, dto.getCurrentPassword(), dto.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
