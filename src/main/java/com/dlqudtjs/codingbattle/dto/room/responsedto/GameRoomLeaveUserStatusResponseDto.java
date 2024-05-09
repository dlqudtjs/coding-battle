package com.dlqudtjs.codingbattle.dto.room.responsedto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomLeaveUserStatusResponseDto {
    private Integer roomId;
    private String userId;
    private Boolean isHost;
}
