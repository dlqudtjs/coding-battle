package com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendToRoomMessageWrapperDto {

    private SendToRoomMessageResponseDto message;
}
