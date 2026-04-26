package auction.exception;

public class DataAccessException extends AppException {

    public DataAccessException(String message){
        super(message, ErrorCode.DATA_ERROR.name());
    }
}
