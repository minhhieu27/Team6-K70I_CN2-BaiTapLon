package com.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPassword {
    
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size (min = 6, max = 100)
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
