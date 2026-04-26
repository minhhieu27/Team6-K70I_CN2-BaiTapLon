package auction.exception;

public class AuctionClosedException extends AppException { // Lỗi kết thúc phiên đấu giá
    public AuctionClosedException(String message){
        super(message, ErrorCode.AUCTION_CLOSED.name());
    }
}
