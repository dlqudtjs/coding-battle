package com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto;

import com.dlqudtjs.codingbattle.dto.game.responseDto.UserSurrenderResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSurrenderMessageResponseDto {

    private UserSurrenderResponseDto userSurrender;
}
