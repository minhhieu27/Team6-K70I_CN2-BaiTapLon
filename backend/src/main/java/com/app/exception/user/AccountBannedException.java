package com.app.exception.user;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class AccountBannedException extends AppException {
    
    public AccountBannedException(String message){
        super(ErrorCode.ACCOUNT_BANNED, message);
    }
}
