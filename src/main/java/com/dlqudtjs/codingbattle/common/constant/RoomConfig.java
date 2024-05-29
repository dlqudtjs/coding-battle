package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomConfig {
    NO_ROOM_ID(-1L),
    DEFAULT_ROOM_ID(0L);

    private final Long value;
}
