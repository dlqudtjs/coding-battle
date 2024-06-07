package com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomUserStatusResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameUserStatusListMessageResponseDto {
    private List<RoomUserStatusResponseDto> userStatusList;
}
