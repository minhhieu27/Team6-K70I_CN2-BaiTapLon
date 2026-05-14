package com.app.exception.security;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class UsernameAlreadyExistException extends AppException {
    public UsernameAlreadyExistException(String message){
        super(ErrorCode.USERNAME_ALREADY_EXISTS, message);
    }
}
