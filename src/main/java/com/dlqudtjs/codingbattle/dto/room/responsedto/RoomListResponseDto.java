package com.dlqudtjs.codingbattle.dto.room.responsedto;

import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomListResponseDto {
    private Long roomId;
    private String hostId;
    private String title;
    private ProgrammingLanguage language;
    private Boolean isLocked;
    private Boolean isStarted;
    private Integer countUsersInRoom;
    private Integer maxUserCount;
    private ProblemLevel problemLevel;
    private Integer maxSubmitCount;
    private Long limitTime;
}
