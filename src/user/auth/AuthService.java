package user.auth;

import java.util.HashMap;

public class AuthService {
    
    private Map<String, User> users = new HashMap<>();

    public void register(User user){
        if (user.containsKey(user.getEmail())){
            throw new RuntimeException("Email đã tồn tại");
        }
        users.put(user.getEmail(), user);
    }

    public User login(String email, String password){
        User.user = users.get(email);

        if (user == null || !user.getPassword().equals(password)){
            throw new RuntimeException("Sai thông tin");
        }

        if (!user.isActive()){
            throw new RuntimeException("Tài khoản bị khóa")
        }

        return user;
    }
}
