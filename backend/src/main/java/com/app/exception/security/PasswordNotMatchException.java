package com.app.exception.security;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class PasswordNotMatchException extends AppException {
    public PasswordNotMatchException(String message){
        super(ErrorCode.INCORRECT_PASSWORD, message);
    }
}
