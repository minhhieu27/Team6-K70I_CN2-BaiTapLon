package com.app.exception.auction;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class AutoBidClosedException extends AppException {
    public AutoBidClosedException(String message){
        super(ErrorCode.AUTOBID_CLOSED, message);
    }
}
