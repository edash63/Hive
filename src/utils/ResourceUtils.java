package utils;

import exception.ErrorCode;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by WS on 26/04/2016.
 */
public final class ResourceUtils {

    private static ResourceBundle bundle = ResourceBundle.getBundle("hive", Locale.getDefault());

    private ResourceUtils() {
        throw new UnsupportedOperationException();
    }

    public static String translate(String resource) {
        return bundle.getString(resource.toLowerCase());
    }

    public static String translate(ErrorCode errorCode) {
        return bundle.getString("error." + errorCode.name().toLowerCase());
    }

    public static void main(String[] args) {
        System.out.println(ResourceUtils.translate(ErrorCode.EMPTY_MOVE));
    }
}
