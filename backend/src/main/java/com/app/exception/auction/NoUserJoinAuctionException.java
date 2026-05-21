package com.app.exception.auction;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.AppException;

public class NoUserJoinAuctionException extends AppException {
    public NoUserJoinAuctionException(String message){
        super(ErrorCode.AUCTION_NO_FOLLOWERS, message);
    }
}
