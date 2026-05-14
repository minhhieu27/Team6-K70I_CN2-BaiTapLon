package com.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank (message = "Password không được để trống")
    public String password;

    @NotBlank (message = "Username không được để trống")
    private String identifier;
}
