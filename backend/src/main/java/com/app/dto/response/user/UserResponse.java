package com.app.dto.response.user;

import java.math.BigDecimal;

import com.app.common.enums.UserStatus;
import com.app.common.enums.VIPLevel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    
    private String userId;
    private String username;
    private String email;
    private String phone;
    private UserStatus status;
    private VIPLevel vipLevel;
    private BigDecimal totalSpent;
}
