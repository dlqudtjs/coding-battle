package com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto;

import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomLeaveUserStatusResponseDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomLeaveUserStatusMessageResponseDto {
    private GameRoomLeaveUserStatusResponseDto leaveUserStatus;
}

