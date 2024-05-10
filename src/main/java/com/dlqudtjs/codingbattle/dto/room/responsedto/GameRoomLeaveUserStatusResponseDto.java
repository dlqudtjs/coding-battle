package com.dlqudtjs.codingbattle.dto.room.responsedto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomLeaveUserStatusResponseDto {
    private Long roomId;
    private String userId;
    private Boolean isHost;
}
