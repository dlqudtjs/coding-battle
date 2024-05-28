package com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.game.responseDto.SubmitDoneResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubmitDoneMessageResponseDto {

    private SubmitDoneResponseDto submitDone;
}
