package com.dlqudtjs.codingbattle.dto.room.responsedto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomUserStatusResponseDto {
    private String userId;
    private Boolean isReady;
    private String language;
}