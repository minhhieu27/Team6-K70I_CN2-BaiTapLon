package com.app.dto.request.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    
    @Email
    @NotBlank
    private String email;

    @Pattern (regexp = "^0[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    @NotBlank
    private String phone;

    @NotBlank
    @Size (min = 6, max = 100)
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
