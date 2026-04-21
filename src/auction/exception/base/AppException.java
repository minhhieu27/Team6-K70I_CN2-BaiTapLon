package auction.exception.base;

public abstract class AppException extends Exception {
    
    private final String errorCode;

    public AppException(String message, String errorCode){
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode(){
        return errorCode;
    }
}
