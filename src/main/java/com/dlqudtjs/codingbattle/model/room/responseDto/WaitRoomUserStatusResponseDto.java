package com.dlqudtjs.codingbattle.model.room.responseDto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WaitRoomUserStatusResponseDto {
    private String userId;
    private Boolean isReady;
    private String language;
}
