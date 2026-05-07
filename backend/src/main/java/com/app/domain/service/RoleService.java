package com.app.domain.service;

import com.app.domain.enums.Role;
import com.app.domain.user.User;;

public class RoleService {
    
    public void becomeBidder(User user){
        user.addRole(Role.ROLE_USER);
    }

    public void becomeSeller(User user){
        user.addRole(Role.ROLE_SELLER);
    }

    public void removeSeller(User user){
        user.removeRole(Role.ROLE_SELLER);
    }
}
