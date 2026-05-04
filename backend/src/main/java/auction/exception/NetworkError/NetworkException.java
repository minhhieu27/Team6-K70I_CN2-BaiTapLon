package auction.exception.NetworkError;

import auction.exception.base.*;

public class NetworkException extends AppException {
    
    public NetworkException(String message){

        super(message, ErrorCode.NETWORK_ERROR.name());
    }
}
