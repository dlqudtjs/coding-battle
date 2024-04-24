package com.dlqudtjs.codingbattle.model.room.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomStatusUpdateMessageResponseDto {
    private GameRoomStatusResponseDto roomStatus;
}
