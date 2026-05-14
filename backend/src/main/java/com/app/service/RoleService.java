package com.app.service;

import org.springframework.stereotype.Service;

import com.app.common.enums.Role;
import com.app.entity.UserEntity;

@Service
public class RoleService {
    
    public void becomeBidder(UserEntity user){
        user.addRole(Role.ROLE_USER);
    }

    public void becomeSeller(UserEntity user){
        user.addRole(Role.ROLE_SELLER);
    }

    public void removeSeller(UserEntity user){
        user.removeRole(Role.ROLE_SELLER);
    }
}
