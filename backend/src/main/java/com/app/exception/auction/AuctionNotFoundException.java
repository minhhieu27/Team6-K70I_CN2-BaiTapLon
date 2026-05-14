package com.app.exception.auction;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class AuctionNotFoundException extends AppException {
    public AuctionNotFoundException(String message){
        super(ErrorCode.AUCTION_NOT_FOUND, message);
    }
}
