package com.dlqudtjs.codingbattle.dto.room.requestdto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SendToRoomMessageRequestDto {
    @NotBlank
    private String senderId;
    @NotBlank
    private String message;
}
