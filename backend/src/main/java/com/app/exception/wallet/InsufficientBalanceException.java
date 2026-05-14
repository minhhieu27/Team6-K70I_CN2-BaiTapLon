package com.app.exception.wallet;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class InsufficientBalanceException extends AppException {
    
    public InsufficientBalanceException(String message){
        super(ErrorCode.INSUFFICIENT_BALANCE, message);
    }
}
