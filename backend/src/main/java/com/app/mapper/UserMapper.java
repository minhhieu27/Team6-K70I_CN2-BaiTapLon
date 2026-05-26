package com.app.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.app.dto.response.user.UserResponse;
import com.app.entity.user.UserEntity;


@Component
public class UserMapper {

    public UserResponse toResponse(UserEntity user){

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getUserProfile().getEmail())
                .phone(user.getUserProfile().getPhone())
                .status(user.getStatus())
                .build();
    }
    
    public List<UserResponse> toResponseList(List<UserEntity> users){

        return users.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
