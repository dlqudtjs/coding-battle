package com.dlqudtjs.codingbattle.dto.room.requestdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomEnterRequestDto {

    @NotBlank
    private Long roomId;
    @NotBlank
    private String userId;
    private String password;
}
