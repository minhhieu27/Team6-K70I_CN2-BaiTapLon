package auction.service;

import java.util.HashMap;
import java.util.Map;

import auction.exception.BusinessError.AccountLockedException;
import auction.exception.SecurrityError.AuthenticationException;
import auction.exception.ValidateError.ValidationException;
import auction.exception.base.AppException;
import auction.tool.Logger;
import users.model.user.*;

public class AuthService {
    
    private final Map<String, User> users = new HashMap<>();
    private final Logger logger = Logger.getInstance();

    public AuthService() {
        users.put("admin", new User("admin", "abc123", false));
        users.put("locked", new User("locked", "abc123", true));
    }

    public User login(String username, String password) throws AppException {

        if (username == null || username.isEmpty()) {
            throw new ValidationException("Username required");
        }

        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password required");
        }

        User user = users.get(username);
        if (user == null){
            throw new AuthenticationException("User not found");
        }

        if (user.isLocked()){
            throw new AccountLockedException("Account is locked");
        }

        logger.info("User logged in: " + username);

        return user;
    }
} 
