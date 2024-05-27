package com.dlqudtjs.codingbattle.dto.game.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameEndResponseDto {

    private String userId;
    private String code;
}
