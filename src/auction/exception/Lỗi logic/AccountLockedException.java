package auction.exception;

import javax.security.auth.login.AccountExpiredException;

public class AccountLockedException extends AppException {
    
    public AccountLockedException(String message){
        super(message, ErrorCode.ACCOUNT_LOCKED.name());
    }
}
