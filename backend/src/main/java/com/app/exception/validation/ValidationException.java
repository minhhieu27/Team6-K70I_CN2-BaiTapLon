package com.app.exception.validation;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.*;

public class ValidationException extends AppException {
    
    public ValidationException(String message){
        super(ErrorCode.VALIDATION_ERROR, message);
    }
}
