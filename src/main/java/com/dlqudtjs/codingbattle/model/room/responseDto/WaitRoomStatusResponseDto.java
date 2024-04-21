package com.dlqudtjs.codingbattle.model.room.responseDto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WaitRoomStatusResponseDto {
    private Integer roomId;
    private String hostId;
    private String title;
    private Boolean isLocked;
    private Integer maxUserCount;
    private Integer problemLevel;
    private Integer maxSummitCount;
    private Integer limitTime;
}
