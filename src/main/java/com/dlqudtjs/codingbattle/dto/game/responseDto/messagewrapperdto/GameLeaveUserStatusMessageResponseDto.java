package com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto;

import com.dlqudtjs.codingbattle.entity.game.LeaveGameUserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameLeaveUserStatusMessageResponseDto {

    private LeaveGameUserStatus leaveUserStatus;
}
