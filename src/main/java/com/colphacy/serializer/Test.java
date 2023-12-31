package com.colphacy.serializer;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Test {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static void main(String[] args) {
        System.out.println(ZonedDateTime.parse("2023-12-31T04:03:00.535Z", FORMATTER));
    }
}
