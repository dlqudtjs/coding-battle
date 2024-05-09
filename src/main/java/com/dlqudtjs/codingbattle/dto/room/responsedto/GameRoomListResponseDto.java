package com.dlqudtjs.codingbattle.dto.room.responsedto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomListResponseDto {
    private Integer roomId;
    private String hostId;
    private String title;
    private String language;
    private Boolean isLocked;
    private Boolean isStarted;
    private Integer countUsersInRoom;
    private Integer maxUserCount;
    private Integer problemLevel;
    private Integer maxSubmitCount;
    private Integer limitTime;
}
