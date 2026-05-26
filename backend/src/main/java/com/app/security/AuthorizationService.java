package com.app.security;

import org.springframework.security.access.AccessDeniedException;

import com.app.common.enums.Role;
import com.app.entity.user.UserEntity;

public class AuthorizationService {
    
    public void checkRole(UserEntity user, Role... roles){
        for (Role role : roles){
            if (user.hasRole(role)){
                return;
            }
        }
        throw new AccessDeniedException("Không có quyền");
    }
}
