package com.dlqudtjs.codingbattle.dto.room.responsedto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomUserStatusResponseDto {
    private String userId;
    private Boolean isReady;
    private String language;
}
