package auction.exception.BusinessError;

import auction.exception.base.*;

public class AccountLockedException extends AppException {
    
    public AccountLockedException(String message){
        super(message, ErrorCode.ACCOUNT_LOCKED.name());
    }
}
