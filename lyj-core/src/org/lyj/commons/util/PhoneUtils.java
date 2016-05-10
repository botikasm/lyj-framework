package org.lyj.commons.util;

/**
 * Phone Number helper
 */
public class PhoneUtils {

    public static boolean isValidNumber(final String number) {
        return RegExpUtils.isValidPhoneNumber(number);
    }

    public static String sanitize(final String number) {
        return StringUtils.replace(number, new String[]{"-", " "}, "");
    }



}
