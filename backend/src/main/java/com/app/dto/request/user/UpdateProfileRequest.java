package com.app.dto.request.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    @Size (max = 100)
    private String username;

    @Pattern (regexp = "^(0|\\+84)[0-9]{9}$")
    private String phone;

    @Size (max = 100)
    private String email;

    @Size (max = 255)
    private String address;

    @Size(max = 500)
    private String bio;

    @Size (max = 500)
    private String avatar;
}
