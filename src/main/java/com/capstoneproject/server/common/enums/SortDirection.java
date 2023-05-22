package com.capstoneproject.server.common.enums;

import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * @author dai.le-anh
 * @since 5/22/2023
 */

public enum SortDirection {
    ASC("asc", "ascending"),
    DESC("desc", "descending"),
    ;

    public final String shortName;
    public final String name;

    SortDirection(String shortName, String name) {
        this.shortName = shortName;
        this.name = name;
    }

    public static boolean isInvalid(String given) {
        return !(ASC.name.equalsIgnoreCase(given) || ASC.shortName.equalsIgnoreCase(given)
                || DESC.name.equalsIgnoreCase(given) || DESC.shortName.equalsIgnoreCase(given));
    }

    public static SortDirection parse(String given) {
        if (!StringUtils.hasText(given)) {
            return ASC;
        }

        return Arrays.stream(values())
                .filter(s -> s.name.equalsIgnoreCase(given) || s.shortName.equalsIgnoreCase(given))
                .findFirst().orElse(ASC);
    }

    public static SortDirection parseStrictly(String given) {
        if (isInvalid(given)) {
            return null;
        }

        return Arrays.stream(values())
                .filter(s -> s.name.equalsIgnoreCase(given) || s.shortName.equalsIgnoreCase(given))
                .findFirst().orElse(null);
    }
}