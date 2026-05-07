package com.app.domain.exception.SecurrityError;

import com.app.domain.exception.base.*;

public class AuthenticationException extends AppException { // Lỗi đăng nhập
    public AuthenticationException(String message){
        super(message, ErrorCode.AUTH_ERROR.name());
    }
}
