package com.app.socket;

public class SocketSession {
    
    private static String token;

    public static void setToken(String jwt){
        token = jwt;
    }

    public static String getToken(){
        return token;
    }
}
