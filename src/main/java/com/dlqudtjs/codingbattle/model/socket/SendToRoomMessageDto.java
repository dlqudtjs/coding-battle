package com.dlqudtjs.codingbattle.model.socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendToRoomMessageDto {

    private String senderId;
    private String message;
}
