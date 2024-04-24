package com.dlqudtjs.codingbattle.model.room.responsedto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomEnterUserStatusMessageResponseDto {
    private GameRoomUserStatusResponseDto enterUserStatus;
}
