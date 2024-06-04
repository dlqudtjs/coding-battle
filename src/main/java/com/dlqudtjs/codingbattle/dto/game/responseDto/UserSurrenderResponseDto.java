package com.dlqudtjs.codingbattle.dto.game.responseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSurrenderResponseDto {

    private String userId;
    private Boolean surrender;
}
