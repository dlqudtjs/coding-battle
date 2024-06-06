package com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.game.responseDto.GameStartResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameStartMessageResponseDto {
    private GameStartResponseDto startMessage;
}
