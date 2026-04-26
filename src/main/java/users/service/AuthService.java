package users.service;

import java.util.HashMap;
import java.util.Map;
import auction.tool.Logger;

import auction.exception.SecurrityError.AuthenticationException;

import auction.exception.BusinessError.AccountLockedException;
import auction.exception.ValidateError.ValidationException;
import auction.exception.base.AppException;
import users.model.user.User;
import users.model.user.UserAccount;

public class AuthService {
    
    private Map<String, User> users = new HashMap<>();
    private final Logger logger = Logger.getInstance();

    public AuthService(){
        users.put("admin", createUser("admin", "admin", "abc123"));
        users.put("locked", createUser("locked", "locked", "abc123"));
    }

    private User createUser(String id, String email, String password){
        return new User(id, new UserAccount(email, password));
    }

    public void register(User user){
        String email = user.getAccount().getEmail();

        if (users.containsKey(email)){
            throw new RuntimeException("Email đã tồn tại");
        }
        users.put(email, user);
    }

    public User login(String email, String password) throws AppException{

        if (email == null || email.isEmpty()){
            throw new ValidationException("Yêu cầu nhập email đăng nhập");
        }

        if (password == null || password.isEmpty()){
            throw new ValidationException("Yêu cầu nhập mật khẩu");
        }

        User user = users.get(email);

        if (user == null){
            throw new AuthenticationException("Không tìm thấy người dùng");
        }

        if (!user.getAccount().isActive()){
            throw new AccountLockedException("Tài khoản bị khóa");
        }

        logger.info("User logged in: " + email);

        return user;
    }
}
