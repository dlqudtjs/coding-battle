package com.dlqudtjs.codingbattle.model.room.responsedto;

import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendToRoomMessageResponseDto {

    private String messageType;
    private String senderId;
    private String message;
    private Timestamp sendTime;
}
