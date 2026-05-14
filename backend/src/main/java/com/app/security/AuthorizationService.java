package com.app.security;

import com.app.common.enums.Role;
import com.app.entity.UserEntity;
import com.app.exception.security.AuthorizationException;

public class AuthorizationService {
    
    public void checkRole(UserEntity user, Role... roles){
        for (Role role : roles){
            if (user.hasRole(role)){
                return;
            }
        }
        throw new AuthorizationException("Không có quyền");
    }
}
