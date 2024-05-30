package com.dlqudtjs.codingbattle.dto.room.responsedto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomInfoResponseDto {
    private RoomStatusResponseDto roomStatus;
    private RoomLeaveUserStatusResponseDto leaveUserStatus;
    private List<RoomUserStatusResponseDto> userStatus;
}
