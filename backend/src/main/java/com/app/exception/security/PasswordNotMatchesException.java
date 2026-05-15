package com.app.exception.security;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class PasswordNotMatchesException extends AppException {
    
    public PasswordNotMatchesException(String message){
        super(ErrorCode.PASSWORD_NOT_MATCHES, message);
    }
}
