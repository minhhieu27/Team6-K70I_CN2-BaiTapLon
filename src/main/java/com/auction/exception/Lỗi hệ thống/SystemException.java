package auction.exception;

public class SystemException {
    
    public SystemException(String message){

        super(message, ErrorCode.SYSTEM_ERROR.name());
    }
}
