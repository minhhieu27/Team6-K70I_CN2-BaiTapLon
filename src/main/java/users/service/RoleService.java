package users.service;

import users.enums.Role;
import users.model.user.User;;

public class RoleService {
    
    public void becomeBidder(User user){
        user.addRole(Role.BIDDER);
    }

    public void becomeSeller(User user){
        user.addRole(Role.SELLER);
    }

    public void removeSeller(User user){
        user.removeRole(Role.SELLER);
    }
}
