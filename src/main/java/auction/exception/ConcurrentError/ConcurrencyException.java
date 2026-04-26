package auction.exception.ConcurrentError;

import auction.exception.base.*;

public class ConcurrencyException extends AppException {

    public ConcurrencyException(String message){
        super(message, ErrorCode.CONCURRENCE_ERROR.name());
    }
}
