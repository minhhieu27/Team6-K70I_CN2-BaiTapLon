package user.model.user;

import java.util.HashSet;
import java.util.Set;

import javax.management.relation.Role;

public class UserRole {
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role){
        roles.add(role);
    }

    public void removeRole(Role role){
        roles.remove(role);
    }

    public boolean hasRole(Role role){ // check xem User Role cụ thể không
        return roles.contains(role); // Contains dùng để check xem có role đó không
    }

    public boolean hasAnyRoke(Role... role){ // Check xem User có ít nhất 1 trong các role không
        // ... dùng để truyền bao nhiêu tham số cũng được
        for (Role r : roles){
            if (this.roles.contains(r)) return true;
        }
        return false;
    }
}
