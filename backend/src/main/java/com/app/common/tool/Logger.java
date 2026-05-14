package com.app.common.tool;

import java.time.LocalDateTime;

public class Logger {
    
    private static Logger instance;

    private Logger(){}

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    public void info(String msg){
        System.out.println(LocalDateTime.now() + " [INFO] " + msg);
    }

    public void error(String msg){
        System.out.println(LocalDateTime.now() + " [ERROR] " + msg);
    }
}
