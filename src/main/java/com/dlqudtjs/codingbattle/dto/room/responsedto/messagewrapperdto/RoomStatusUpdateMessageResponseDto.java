package com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomStatusResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomStatusUpdateMessageResponseDto {
    private RoomStatusResponseDto roomStatus;
}
