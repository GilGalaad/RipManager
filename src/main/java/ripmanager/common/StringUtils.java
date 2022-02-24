package ripmanager.common;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.trim().equals("");
    }

    public static String ltrim(String s) {
        return s.replaceAll("^\\s+", "");
    }

    public static String rtrim(String s) {
        return s.replaceAll("\\s+$", "");
    }

}
