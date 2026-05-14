package com.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank (message = "Username không được để trống")
    private String username;

    @Email (message = "Email không hợp lệ")
    @NotBlank (message = "Email không hợp lệ")
    private String email;
    
    @NotBlank (message = "Password không được trống")
    @Size (min = 8, message = "Password tối thiểu 8 ý tự")
    private String password;

    @Pattern (regexp = "^0[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    @NotBlank(message = "Số điện thoại không được trống")
    private String phone;

}
