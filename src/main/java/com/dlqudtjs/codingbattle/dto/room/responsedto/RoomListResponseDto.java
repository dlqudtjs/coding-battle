package com.dlqudtjs.codingbattle.dto.room.responsedto;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomListResponseDto {
    private Long roomId;
    private String hostId;
    private String title;
    private String language;
    private Boolean isLocked;
    private Boolean isStarted;
    private Integer countUsersInRoom;
    private Integer maxUserCount;
    private ProblemLevelType problemLevel;
    private Integer maxSubmitCount;
    private Long limitTime;
}
