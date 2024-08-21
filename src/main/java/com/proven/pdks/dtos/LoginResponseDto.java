package com.proven.pdks.dtos;

import com.proven.pdks.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private boolean isAuthenticated;
    private String tckn;
    private String token;
    private Role role;
    private String fullName;
}
