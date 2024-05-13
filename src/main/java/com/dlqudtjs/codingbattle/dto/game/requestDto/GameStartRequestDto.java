package com.dlqudtjs.codingbattle.dto.game.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GameStartRequestDto {

    @NotBlank
    private Long roomId;
}
