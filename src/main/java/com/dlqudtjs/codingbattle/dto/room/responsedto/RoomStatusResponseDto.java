package com.dlqudtjs.codingbattle.dto.room.responsedto;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomStatusResponseDto {
    private Long roomId;
    private String hostId;
    private String title;
    private ProgrammingLanguage language;
    private Boolean isLocked;
    private Boolean isStarted;
    private Integer maxUserCount;
    private ProblemLevelType problemLevel;
    private Integer maxSubmitCount;
    private Long limitTime;
}
