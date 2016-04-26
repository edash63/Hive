package utils;

import exception.ErrorCode;
import exception.HiveException;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by EH053 on 26/04/2016.
 */
public final class ExceptionUtils {

    private static ResourceBundle bundle = ResourceBundle.getBundle("hive", Locale.getDefault());

    private ExceptionUtils() {
        throw new UnsupportedOperationException();
    }

    public static String translate(HiveException e) {
        return bundle.getString("error." + e.getReason().name().toLowerCase());
    }

    public static void main(String[] args) {
        System.out.println(ExceptionUtils.translate(new HiveException(ErrorCode.WRONG_MOVE)));
    }
}
