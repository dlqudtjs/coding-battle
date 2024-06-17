package com.dlqudtjs.codingbattle.entity.match;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchRecode {

    private List<MatchRecodeUserStatus> usersResult;
    private Long matchId;
    private ProgrammingLanguage language;
    private MatchingResultType result;
    private ProblemLevelType problemLevel;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private String elapsedTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime date;
}
