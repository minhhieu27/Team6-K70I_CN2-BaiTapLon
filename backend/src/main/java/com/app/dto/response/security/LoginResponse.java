package com.app.dto.response.security;

import java.util.Set;

import com.app.common.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private String userId;

    private String username;

    private Set<Role> roles;
}
