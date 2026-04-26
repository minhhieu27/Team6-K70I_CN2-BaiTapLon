package auction.exception;

import java.util.logging.Logger;

import auction.exception.base.AppException;

public class GlobalExceptionHandler {
    
    public static void handle(Exception e) {
        Logger logger = Logger.getInstance();

        if (e instanceof AppException) {
            AppException ex = (AppException) e;

            logger.error("Code: " + ex.getErrorCode());
            logger.error("Message: " + ex.getMessage());

        } else {
            logger.error("UNKOWN ERROR");
            e.printStackTrace();
        }
    }
}
