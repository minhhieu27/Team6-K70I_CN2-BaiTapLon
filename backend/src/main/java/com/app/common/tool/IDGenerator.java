 package com.app.common.tool;

import java.util.UUID;

public class IDGenerator {
    
    public static String generateUserId(){ // Tạo ID random
        return "USR-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateAuctionId(){
        return "AUC-" + UUID.randomUUID().toString().substring(0,9);
    }
}
