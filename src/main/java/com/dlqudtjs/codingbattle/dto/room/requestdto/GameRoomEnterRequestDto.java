package com.dlqudtjs.codingbattle.dto.room.requestdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class GameRoomEnterRequestDto {

    @NotBlank
    private String userId;
    @NotBlank
    private Long roomId;
}
