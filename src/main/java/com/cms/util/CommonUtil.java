package com.cms.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;


public class CommonUtil {
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    public static String generateUniqueSlug(String name) {
        String baseSlug = toSlug(name);

        // random 6 chars
        String uniquePart = UUID.randomUUID().toString()
                .substring(0, 6);

        return baseSlug + "-" + uniquePart;
    }

    public static String toSlug(String input) {
        if (input == null || input.isBlank()) return "";

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        slug = slug.replace("đ", "d").replace("Đ", "D");

        slug = slug.toLowerCase(Locale.ENGLISH);

        slug = WHITESPACE.matcher(slug).replaceAll("-");

        slug = NON_LATIN.matcher(slug).replaceAll("");

        slug = slug.replaceAll("-{2,}", "-");

        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }

}
