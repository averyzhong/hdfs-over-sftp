package com.avery.hdfs.sftp;

import java.util.Arrays;

/**
 * @author AveryZhong.
 */

public class StringUtils {
    private StringUtils() {
        throw new AssertionError("No instance for you.");
    }

    public static String concat(final Object... objects) {
        if (objects == null) {
            return "";
        }
        return Arrays.stream(objects)
                .map(Object::toString)
                .reduce((str1, str2) -> str1 + str2)
                .orElse("");
    }

    public static String toString(final Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    public static boolean isEmpty(final String str) {
        return str == null || str.equals("");
    }


}
