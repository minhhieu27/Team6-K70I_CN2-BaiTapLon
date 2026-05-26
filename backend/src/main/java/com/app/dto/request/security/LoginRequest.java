package com.app.dto.request.security;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank (message = "Username không được để trống")
    private String identifier;

    @NotBlank (message = "Password không được để trống")
    public String password;
}
