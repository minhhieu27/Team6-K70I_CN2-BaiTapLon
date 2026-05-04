package auction.exception.SystemError;

import auction.exception.base.*;

public class SystemException extends AppException {
    
    public SystemException(String message){

        super(message, ErrorCode.SYSTEM_ERROR.name());
    }
}
