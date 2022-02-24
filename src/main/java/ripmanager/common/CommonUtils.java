package ripmanager.common;

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

}
