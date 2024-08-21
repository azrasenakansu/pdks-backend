package com.proven.pdks.services;

import com.proven.pdks.dtos.LoginDto;
import com.proven.pdks.dtos.LoginResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthanticationService {
    LoginResponseDto login(LoginDto loginDto);

    void resetPassword(String tckn);

    void changePassword(String username, String currentPassword, String newPassword);
}
