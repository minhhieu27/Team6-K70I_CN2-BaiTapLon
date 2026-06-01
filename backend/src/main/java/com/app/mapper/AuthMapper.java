package com.app.mapper;

import java.util.stream.Collectors; // Nhớ import cái này
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
                // THÊM DÒNG NÀY ĐỂ MAP QUYỀN (ROLES) CỦA USER VÀO RESPONSE
                .roles(user.getRoles().stream().map(r -> r.getRole()).collect(Collectors.toSet()))
                .build();
    }
}