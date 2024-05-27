package com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.game.responseDto.GameEndResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameEndMessageResponseDto {
    private GameEndResponseDto gameEnd;
}
