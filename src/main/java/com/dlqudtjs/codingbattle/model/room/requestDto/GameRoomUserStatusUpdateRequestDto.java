package com.dlqudtjs.codingbattle.model.room.requestDto;

import lombok.Getter;

@Getter
public class GameRoomUserStatusUpdateRequestDto {
    private String userId;
    private Boolean isReady;
    private String language;
}
