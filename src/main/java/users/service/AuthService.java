package users.service;

import java.util.HashMap;
import java.util.Map;

import users.model.user.User;

public class AuthService {
    
    private Map<String, User> users = new HashMap<>();

    public void register(User user){
        if (users.containsKey(user.getEmail())){
            throw new RuntimeException("Email đã tồn tại");
        }
        users.put((String) user.getEmail(), user);
    }

    public User login(String email, String password){
        User user = ((Object) users).getAcount().getEmail();

        if (user == null || !((Object) users).getAccount().getPassword().equals(password)){
            throw new RuntimeException("Sai thông tin");
        }

        if (!user.isActive()){
            throw new RuntimeException("Tài khoản bị khóa");
        }
    }
}
