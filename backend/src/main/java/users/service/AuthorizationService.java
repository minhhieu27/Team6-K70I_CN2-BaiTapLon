package users.service;

import users.enums.Role;
import users.model.user.User;

public class AuthorizationService {
    
    public void checkRole(User user, Role... roles){
        for (Role role : roles){
            if (!user.hasRole(role)){
                throw new RuntimeException("Không có quyền");
            }
        }
    }
}
