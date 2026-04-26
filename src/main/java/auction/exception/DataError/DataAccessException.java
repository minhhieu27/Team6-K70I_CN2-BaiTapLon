package auction.exception.DataError;

import auction.exception.base.*;

public class DataAccessException extends AppException {

    public DataAccessException(String message){
        super(message, ErrorCode.DATA_ERROR.name());
    }
}
