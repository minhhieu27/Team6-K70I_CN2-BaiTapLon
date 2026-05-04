package auction.exception.SecurrityError;

import auction.exception.base.*;

public class AuthorizationException extends AppException {
    
    public AuthorizationException(String message){
        super(message, ErrorCode.ACCESS_DINED.name());
    }
}
