package com.app.exception.user;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.*;

public class AccountLockedException extends AppException {
    
    public AccountLockedException(String message){
        super(ErrorCode.ACCOUNT_LOCKED, message);
    }
}
