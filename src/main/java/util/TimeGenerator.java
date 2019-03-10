package util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeGenerator {
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Timestamp currentTime() {
        return Timestamp
                .valueOf(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern(TIME_FORMAT)));
    }
}
