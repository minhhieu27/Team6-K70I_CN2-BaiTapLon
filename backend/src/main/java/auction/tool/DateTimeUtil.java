package auction.tool;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateTimeUtil {
    
    public static long secondLeft(LocalDateTime end) {
        return Duration.between(LocalDateTime.now(), end).getSeconds();
    }

}
