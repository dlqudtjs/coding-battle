package com.dlqudtjs.codingbattle.dto.room.responsedto;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomUserStatusResponseDto {
    private String userId;
    private Boolean isReady;
    private ProgrammingLanguage language;
}
