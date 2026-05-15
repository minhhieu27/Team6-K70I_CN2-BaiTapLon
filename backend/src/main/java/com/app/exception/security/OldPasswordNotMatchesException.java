package com.app.exception.security;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class OldPasswordNotMatchesException extends AppException {
    public OldPasswordNotMatchesException(String message){
        super(ErrorCode.OLD_PASSWORD_INCORRECT, message);
    }
}
