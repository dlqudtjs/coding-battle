package com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto;

import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomStatusResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomStatusUpdateMessageResponseDto {
    private GameRoomStatusResponseDto roomStatus;
}
