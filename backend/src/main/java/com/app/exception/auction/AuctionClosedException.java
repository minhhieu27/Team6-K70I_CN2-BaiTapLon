package com.app.exception.auction;

import com.app.common.enums.ErrorCode;
import com.app.exception.base.*;

public class AuctionClosedException extends AppException { // Lỗi kết thúc phiên đấu giá
    public AuctionClosedException(String message){
        super(ErrorCode.AUCTION_CLOSED, message);
    }
}
