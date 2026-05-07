package com.app.domain.service;

import com.app.domain.enums.Role;
import com.app.domain.user.User;

public class AuthorizationService {
    
    public void checkRole(User user, Role... roles){
        for (Role role : roles){
            if (!user.hasRole(role)){
                throw new RuntimeException("Không có quyền");
            }
        }
    }
}
