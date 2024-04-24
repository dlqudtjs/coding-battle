package com.dlqudtjs.codingbattle.model.room.responsedto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomLeaveUserStatusResponseDto {
    private String userId;
    private Boolean isHost;
}
