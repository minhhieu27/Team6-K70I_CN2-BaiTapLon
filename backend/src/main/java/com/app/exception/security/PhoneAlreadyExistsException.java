package com.app.exception.security;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class PhoneAlreadyExistsException extends AppException {
    public PhoneAlreadyExistsException(String message){
        super(ErrorCode.PHONE_ALREADY_EXISTS, message);
    }
}
