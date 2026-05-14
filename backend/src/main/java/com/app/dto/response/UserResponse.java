package com.app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    
    private String userId;
    private String username;
    private String email;
    private String phone;
    private boolean active;
}
