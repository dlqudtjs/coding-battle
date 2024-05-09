package com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomLeaveUserStatusResponseDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomLeaveUserStatusMessageResponseDto {
    private GameRoomLeaveUserStatusResponseDto leaveUserStatus;
}

