package com.dlqudtjs.codingbattle.model.room.responsedto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomStatusResponseDto {
    private Integer roomId;
    private String hostId;
    private String title;
    private String language;
    private Boolean isLocked;
    private Boolean isStarted;
    private Integer maxUserCount;
    private Integer problemLevel;
    private Integer maxSubmitCount;
    private Integer limitTime;
}
