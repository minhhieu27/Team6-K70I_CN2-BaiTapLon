package com.app.domain.exception.ValidateError;

import com.app.domain.exception.base.*;

public class ValidationException extends AppException {
    
    public ValidationException(String message){
        super(message, ErrorCode.VALIDATION_ERROR.name());
    }
}
