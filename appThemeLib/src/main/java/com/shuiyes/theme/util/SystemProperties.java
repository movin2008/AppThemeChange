package com.shuiyes.theme.util;

import java.lang.reflect.Method;

public class SystemProperties {
    private static final String TAG = "SystemProperties";

    /**
     * Get the value for the given key.
     *
     * @return an empty string if the key isn't found
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static String get(String key) {
        String value = null;
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method m = c.getDeclaredMethod("get", String.class);
            value = (String) m.invoke(c, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Get the value for the given key.
     *
     * @return if the key isn't found, return def if it isn't null, or an empty
     * string otherwise
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static String get(String key, String def) {
        String value = def;
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method m = c.getDeclaredMethod("get", String.class, String.class);
            value = (String) m.invoke(c, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Get the value for the given key, and return as an integer.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as an integer, or def if the key isn't found or
     * cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static int getInt(String key, int def) {
        int value = def;
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method m = c.getDeclaredMethod("getInt", String.class, int.class);
            value = (Integer) m.invoke(c, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Get the value for the given key, and return as a long.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a long, or def if the key isn't found or cannot
     * be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static long getLong(String key, long def) {
        long value = def;
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method m = c.getDeclaredMethod("getLong", String.class, long.class);
            value = (Long) m.invoke(c, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Get the value for the given key, returned as a boolean. Values 'n', 'no',
     * '0', 'false' or 'off' are considered false. Values 'y', 'yes', '1',
     * 'true' or 'on' are considered true. (case insensitive). If the key does
     * not exist, or has any other value, then the default result is returned.
     *
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a boolean, or def if the key isn't found or is
     * not able to be parsed as a boolean.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static boolean getBoolean(String key, boolean def) {
        boolean value = def;
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method m = c.getDeclaredMethod("getBoolean", String.class, boolean.class);
            value = (Boolean) m.invoke(c, key, def);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Set the value for the given key.
     *
     * @throws IllegalArgumentException if the key exceeds 32 characters
     * @throws IllegalArgumentException if the value exceeds 92 characters
     */
    public static void set(String key, String val) {
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method m = c.getDeclaredMethod("set", String.class, String.class);
            m.invoke(null, key, val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
