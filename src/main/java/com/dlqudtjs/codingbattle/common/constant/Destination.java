package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Destination {
    ROOM_BROADCAST("/topic/rooms/"),
    ;

    private final String value;

    public static final String ROOM_BROADCAST_VALUE = "/topic/rooms/";
}
