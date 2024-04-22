package com.dlqudtjs.codingbattle.model.room.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GameRoomEnterRequestDto {

    @NotBlank
    private String userId;
    @NotBlank
    private Integer roomId;
}
