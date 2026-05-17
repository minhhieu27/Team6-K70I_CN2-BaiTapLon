package com.app.exception.user;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class UserNotFoundException extends AppException {
    
    public UserNotFoundException(String message){
        super(ErrorCode.USER_NOT_FOUND, message);
    }
}
