package com.app.exception.security;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.*;

public class LoginException extends AppException { // Lỗi đăng nhập
    public LoginException(String message){
        super(ErrorCode.LOGIN_ERROR, message);
    }
}
