package auction.tool;

import java.util.UUID;

public class IDGenerator {
    
    public static String generateId(){ // Tạo ID random
        return UUID.randomUUID().toString().substring(0, 9);
    }

}
