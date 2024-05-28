package com.dlqudtjs.codingbattle.dto.room.responsedto;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomStatusResponseDto {
    private Long roomId;
    private String hostId;
    private String title;
    private String language;
    private Boolean isLocked;
    private Boolean isStarted;
    private Integer maxUserCount;
    private ProblemLevelType problemLevel;
    private Integer maxSubmitCount;
    private Long limitTime;
}
