package com.dlqudtjs.codingbattle.model.room.requestDto;

import lombok.Getter;

@Getter
public class GameRoomStatusUpdateRequestDto {
    private String hostId;
    private String title;
    private String password;
    private String language;
    private Integer problemLevel;
    private Integer maxUserCount;
    private Integer maxSubmitCount;
    private Integer limitTime;
}
