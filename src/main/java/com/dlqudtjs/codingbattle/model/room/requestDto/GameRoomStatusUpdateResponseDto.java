package com.dlqudtjs.codingbattle.model.room.requestDto;

import com.dlqudtjs.codingbattle.model.room.responseDto.GameRoomStatusResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomStatusUpdateResponseDto {
    private GameRoomStatusResponseDto roomStatus;
}
