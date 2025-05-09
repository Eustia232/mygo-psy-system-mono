package com.mygo.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MessageType implements ValueEnum {
    TEXT("text"),
    PHOTO("photo"),
    FILE("file");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }
}
