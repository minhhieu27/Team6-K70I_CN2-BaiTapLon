package com.app.exception.security;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class EmailAlreadyExistxException extends AppException {
    public EmailAlreadyExistxException(String message){
        super(ErrorCode.EMAIL_ALREADY_EXISTS, message);
    }
}
