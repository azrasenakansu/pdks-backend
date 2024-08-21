package com.proven.pdks.services;

import com.proven.pdks.dtos.LoginDto;
import com.proven.pdks.dtos.LoginResponseDto;
import com.proven.pdks.entities.User;
import com.proven.pdks.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthanticationServiceImpl implements AuthanticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        LoginResponseDto response = new LoginResponseDto();
        User user = userRepository.findByTckn(loginDto.getUsername());
        boolean isAuthenticated = user != null && passwordEncoder.matches(loginDto.getPassword(), user.getPassword());
        if (!isAuthenticated) {
            return response;
        }
        response.setAuthenticated(true);
        response.setTckn(user.getTckn());
        response.setToken(jwtService.GenerateToken(user.getTckn()));
        response.setRole(user.getRole());
        response.setFullName(user.getName());
        return response;
    }

    @Override
    public void resetPassword(String tckn) {
        User user = userRepository.findByTckn(tckn);
        if (user == null) {
            throw new RuntimeException("User not found with TCKN: " + tckn);
        }
        user.setPassword(passwordEncoder.encode(tckn));
        userRepository.save(user);
    }

    @Override
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByTckn(username);
        if (user == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
