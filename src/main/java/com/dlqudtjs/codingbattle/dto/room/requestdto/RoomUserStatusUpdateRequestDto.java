package com.dlqudtjs.codingbattle.dto.room.requestdto;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomUserStatusUpdateRequestDto {
    @NotNull
    private String userId;
    @NotNull
    private Boolean isReady;
    @NotNull
    private ProgrammingLanguage language;
}
