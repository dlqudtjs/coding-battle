package com.dlqudtjs.codingbattle.dto.room.responsedto;

import com.dlqudtjs.codingbattle.common.constant.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendToRoomMessageResponseDto {

    private MessageType messageType;
    private String senderId;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private ZonedDateTime sendTime;
}
