package com.dlqudtjs.codingbattle.dto.game.requestDto;

import com.dlqudtjs.codingbattle.entity.submit.SubmitResultCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateSubmitResultRequestDto {

    private Long executionTime;
    private SubmitResultCode submitResultCode;
}
