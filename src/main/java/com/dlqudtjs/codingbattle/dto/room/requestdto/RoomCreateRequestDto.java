package com.dlqudtjs.codingbattle.dto.room.requestdto;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomCreateRequestDto {

    @NotNull
    private String hostId;

    @NotNull
    private String title;

    @NotNull
    private String password;

    @NotNull
    private ProgrammingLanguage language;

    @NotNull
    private ProblemLevelType problemLevel;

    private Integer maxUserCount;

    private Integer maxSubmitCount;

    private Long limitTime;
}
