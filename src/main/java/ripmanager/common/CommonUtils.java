package ripmanager.common;

import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@Log4j2
public class CommonUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    public static String ltrim(String s) {
        return s.replaceAll("^\\s+", "");
    }

    public static String rtrim(String s) {
        return s.replaceAll("\\s+$", "");
    }

    public static Long calcEta(long startTime, int percent) {
        switch (percent) {
            case 0:
                return null;
            case 100:
                return 0L;
            default:
                long elapsed = System.nanoTime() - startTime;
                long total = elapsed * 100L / percent;
                return (total - elapsed) / 1_000_000_000L;
        }
    }

    public static String formatInterval(long interval) {
        long hours = interval / 3600L;
        long minutes = (interval % 3600L) / 60L;
        long seconds = interval % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static long parseInterval(String interval) {
        String[] split = interval.split("[:\\.]");
        long hours = Long.parseLong(split[0]);
        long minutes = Long.parseLong(split[1]);
        long seconds = Long.parseLong(split[2]);
        long cents = Long.parseLong(split[3]);
        long ret = hours * 3600 + minutes * 60 + seconds;
        if (cents >= 50L) {
            ret += 1L;
        }
        return ret;
    }

    public static int calcPercent(long currentTime, long totalTime) {
        if (totalTime == 0) {
            return 0;
        }
        return BigDecimal.valueOf(currentTime).multiply(BigDecimal.valueOf(100L)).divide(BigDecimal.valueOf(totalTime), 0, RoundingMode.HALF_UP).toBigInteger().intValue();
    }

    public static String smartElapsed(long elapsedNano) {
        return smartElapsed(elapsedNano, 2);
    }

    public static String smartElapsed(long elapsedNano, int scale) {
        if (elapsedNano < TimeUnit.MICROSECONDS.toNanos(1)) {
            return elapsedNano + " nsec";
        } else if (elapsedNano < TimeUnit.MILLISECONDS.toNanos(1)) {
            return BigDecimal.valueOf(elapsedNano).divide(BigDecimal.valueOf(TimeUnit.MICROSECONDS.toNanos(1)), scale, RoundingMode.HALF_UP) + " usec";
        } else if (elapsedNano < TimeUnit.SECONDS.toNanos(1)) {
            return BigDecimal.valueOf(elapsedNano).divide(BigDecimal.valueOf(TimeUnit.MILLISECONDS.toNanos(1)), scale, RoundingMode.HALF_UP) + " msec";
        } else if (elapsedNano < TimeUnit.MINUTES.toNanos(1)) {
            return BigDecimal.valueOf(elapsedNano).divide(BigDecimal.valueOf(TimeUnit.SECONDS.toNanos(1)), scale, RoundingMode.HALF_UP) + " sec";
        } else if (elapsedNano < TimeUnit.HOURS.toNanos(1)) {
            return BigDecimal.valueOf(elapsedNano).divide(BigDecimal.valueOf(TimeUnit.MINUTES.toNanos(1)), scale, RoundingMode.HALF_UP) + " min";
        } else {
            return BigDecimal.valueOf(elapsedNano).divide(BigDecimal.valueOf(TimeUnit.HOURS.toNanos(1)), scale, RoundingMode.HALF_UP) + " hours";
        }
    }

}
