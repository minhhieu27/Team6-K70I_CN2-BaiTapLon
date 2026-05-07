package com.app.domain.exception;

import java.util.logging.Logger;

import com.app.domain.exception.base.AppException;

public class GlobalExceptionHandler {

    private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());
    
    public static void handle(Exception e) {

        if (e instanceof AppException) {
            AppException ex = (AppException) e;

            logger.severe("Code: " + ex.getErrorCode());
            logger.severe("Message: " + ex.getMessage());

        } else {
            logger.severe("UNKOWN ERROR");
            e.printStackTrace();
        }
    }
}
