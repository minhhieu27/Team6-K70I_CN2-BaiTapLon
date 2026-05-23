package com.app.exception.auction;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class AutoBidNotFoundException extends AppException{
    public AutoBidNotFoundException(String message){
        super(ErrorCode.AUTOBID_NOT_FOUND, message);
    }
}
