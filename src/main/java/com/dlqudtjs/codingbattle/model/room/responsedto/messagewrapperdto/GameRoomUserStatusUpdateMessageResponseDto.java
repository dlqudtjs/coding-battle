package com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto;

import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomUserStatusResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomUserStatusUpdateMessageResponseDto {
    private GameRoomUserStatusResponseDto updateUserStatus;
}
