package com.dlqudtjs.codingbattle.model.room.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomUserStatusEnterResponseDto {
    private GameRoomUserStatusResponseDto enterUserStatus;
}
