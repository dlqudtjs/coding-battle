package com.dlqudtjs.codingbattle.model.room.responseDto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WaitRoomResponseDto {
    private WaitRoomStatusResponseDto roomStatus;
    private List<WaitRoomUserStatusResponseDto> userStatus;
}
