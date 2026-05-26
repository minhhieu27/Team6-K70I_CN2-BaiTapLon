package com.app.mapper;

import org.springframework.stereotype.Component;

import com.app.dto.response.security.LoginResponse;
import com.app.entity.user.UserEntity;

@Component
public class AuthMapper {
    
    public LoginResponse toLoginResponse(UserEntity user, String token){

        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .username(user.getUsername())
                .build();
    }
}
