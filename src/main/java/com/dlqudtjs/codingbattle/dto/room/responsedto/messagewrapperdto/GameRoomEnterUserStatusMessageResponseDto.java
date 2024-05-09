package com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomUserStatusResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomEnterUserStatusMessageResponseDto {
    private GameRoomUserStatusResponseDto enterUserStatus;
}
