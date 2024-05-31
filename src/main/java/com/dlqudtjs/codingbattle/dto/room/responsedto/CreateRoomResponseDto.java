package com.dlqudtjs.codingbattle.dto.room.responsedto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateRoomResponseDto {
    private RoomStatusResponseDto roomStatus;
    private List<RoomUserStatusResponseDto> userStatus;
}
