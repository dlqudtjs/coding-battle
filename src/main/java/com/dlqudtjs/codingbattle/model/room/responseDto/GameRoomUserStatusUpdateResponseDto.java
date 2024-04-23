package com.dlqudtjs.codingbattle.model.room.responseDto;

import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomUserStatusUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomUserStatusUpdateResponseDto {
    private GameRoomUserStatusUpdateRequestDto userStatus;
}
