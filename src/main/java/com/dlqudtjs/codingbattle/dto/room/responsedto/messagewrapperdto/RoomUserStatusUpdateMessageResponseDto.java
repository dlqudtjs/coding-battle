package com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomUserStatusResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomUserStatusUpdateMessageResponseDto {
    private RoomUserStatusResponseDto updateUserStatus;
}
