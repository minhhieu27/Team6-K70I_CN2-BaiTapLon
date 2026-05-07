package com.app.domain.service;

import java.util.HashMap;
import java.util.Map;

import com.app.domain.exception.BusinessError.AccountLockedException;
import com.app.domain.exception.SecurrityError.AuthenticationException;
import com.app.domain.exception.ValidateError.ValidationException;
import com.app.domain.exception.base.AppException;
import com.app.domain.tool.Logger;
import com.app.domain.user.User;
import com.app.domain.user.UserAccount;

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
