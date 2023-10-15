package com.colphacy.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {
    private StringUtils() {
    }

    public static String slugify(String str) {
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        temp = pattern.matcher(temp).replaceAll("");
        temp = temp.toLowerCase();
        temp = temp.replace("đ", "d");
        temp = temp.replaceAll("[^\\w\\s-]", "");  // Remove all non-word, non-space or non-hyphen characters
        temp = temp.replaceAll("[-\\s]+", "-"); // Replace spaces, non-word or non-number characters with hyphens
        return temp;
    }

    public static String seperateBySpace(String slug) {
        return slug.replace("-", " ");
    }
}
