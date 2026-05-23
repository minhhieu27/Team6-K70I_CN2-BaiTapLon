package com.app.common.tool;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateTimeUtil {
    
    public static long secondLeft(LocalDateTime end) { // Đếm ngược thời gian
        return Duration.between(LocalDateTime.now(), end).getSeconds();
    }

    public static boolean passSeconds(LocalDateTime time, long seconds){ // Kiểm tra xem đã qua bao lâu kể từ lần trước đó

        if (time == null){
            return true;
        }

        return Duration.between(time, LocalDateTime.now()).getSeconds() >= seconds;
    }
}
