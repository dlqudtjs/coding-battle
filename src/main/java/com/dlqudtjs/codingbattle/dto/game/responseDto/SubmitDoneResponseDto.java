package com.dlqudtjs.codingbattle.dto.game.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubmitDoneResponseDto {

    private String userId;
    private Boolean submitDone;
}
