package com.app.domain.exception.BusinessError;

import com.app.domain.exception.base.*;

public class AccountLockedException extends AppException {
    
    public AccountLockedException(String message){
        super(message, ErrorCode.ACCOUNT_LOCKED.name());
    }
}
