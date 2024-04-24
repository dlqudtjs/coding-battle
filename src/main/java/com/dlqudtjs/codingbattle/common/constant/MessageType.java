package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {

    SYSTEM("SYSTEM"),
    USER("USER"),
    ;

    private final String messageType;
}
