package com.cms.util;

import java.util.Collection;
import java.util.Map;

/**
 * Utility class for basic validation checks (null, empty, etc.)
 */
public class ValidationUtil {

    // Prevent instantiation
    private ValidationUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Check if an object is null.
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * Check if an object is not null.
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    /**
     * Check if a string is null or empty (including whitespace-only strings).
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Check if a string is not null and not empty.
     */
    public static boolean isNotNullOrEmpty(String str) {
        return !isNullOrEmpty(str);
    }

    /**
     * Check if a collection is null or empty.
     */
    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Check if a collection is not null and not empty.
     */
    public static boolean isNotNullOrEmpty(Collection<?> collection) {
        return !isNullOrEmpty(collection);
    }

    /**
     * Check if a map is null or empty.
     */
    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Check if a map is not null and not empty.
     */
    public static boolean isNotNullOrEmpty(Map<?, ?> map) {
        return !isNullOrEmpty(map);
    }

    /**
     * Check if an array is null or empty.
     */
    public static <T> boolean isNullOrEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Check if an array is not null and not empty.
     */
    public static <T> boolean isNotNullOrEmpty(T[] array) {
        return !isNullOrEmpty(array);
    }

    /**
     * Check if an object is null or empty. 
     * Supports String, Collection, Map, and Arrays.
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).trim().isEmpty();
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        if (obj.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(obj) == 0;
        }
        return false;
    }
    
    /**
     * Check if an object is not null and not empty. 
     * Supports String, Collection, Map, and Arrays.
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }
}
