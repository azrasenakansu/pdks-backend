package com.proven.pdks.common;

import com.fasterxml.jackson.annotation.JsonValue;
import com.proven.pdks.exceptionHandling.WillfullException;

public enum ExternalWorklogType {
    OTHER(0), HYBRID(1), ASELSAN(2);

    private final int value;

    ExternalWorklogType(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public static ExternalWorklogType fromValue(int value) {
        return switch (value) {
            case 0 -> OTHER;
            case 1 -> HYBRID;
            case 2 -> ASELSAN;
            default -> throw new WillfullException("Unknown value: " + value);
        };
    }
}
