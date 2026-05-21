package com.app.exception.auction;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class AutoBidAlreadyExistsException extends AppException{
    public AutoBidAlreadyExistsException(String message){
        super(ErrorCode.AUTOBID_ALREADY_EXISTS, message);
    }
}
