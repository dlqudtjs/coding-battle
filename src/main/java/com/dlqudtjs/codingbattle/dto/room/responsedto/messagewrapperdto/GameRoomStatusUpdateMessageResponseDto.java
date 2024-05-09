package com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomStatusResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomStatusUpdateMessageResponseDto {
    private GameRoomStatusResponseDto roomStatus;
}
