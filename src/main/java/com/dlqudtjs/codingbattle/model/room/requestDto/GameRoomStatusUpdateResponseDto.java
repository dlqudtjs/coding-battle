package com.dlqudtjs.codingbattle.model.room.requestDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomStatusUpdateResponseDto {
    private GameRoomStatusUpdateRequestDto roomStatus;
}
