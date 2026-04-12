package auction.exception;

public class AuctionClosedException extends Exception { // Lỗi kết thúc phiên đấu giá
    public AuctionClosedException(String msg){
        super(msg);
    }
}
