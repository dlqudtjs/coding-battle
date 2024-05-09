package com.dlqudtjs.codingbattle.dto.room.responsedto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRoomInfoResponseDto {
    private GameRoomStatusResponseDto roomStatus;
    private GameRoomLeaveUserStatusResponseDto leaveUserStatus;
    private List<GameRoomUserStatusResponseDto> userStatus;
}
