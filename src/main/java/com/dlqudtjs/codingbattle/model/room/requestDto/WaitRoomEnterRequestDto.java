package com.dlqudtjs.codingbattle.model.room.requestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WaitRoomEnterRequestDto {

    @NotBlank
    private String userId;
    @NotBlank
    private Integer roomId;
}
