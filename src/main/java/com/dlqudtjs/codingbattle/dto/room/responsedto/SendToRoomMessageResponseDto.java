package com.dlqudtjs.codingbattle.dto.room.responsedto;

import com.dlqudtjs.codingbattle.common.constant.MessageType;
import java.sql.Timestamp;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendToRoomMessageResponseDto {

    private MessageType messageType;
    private String senderId;
    private String message;
    private Timestamp sendTime;
}
