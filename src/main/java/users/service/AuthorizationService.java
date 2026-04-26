package users.service;

import users.enums.Role;
import users.model.user.User;

public class AuthorizationService {
    
    public void checkRole(User user, Role... roles){
        if (!user.hasAnyRole(roles)){
            throw new RuntimeException("Không có quyền");
        }
    }
}
