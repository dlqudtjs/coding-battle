package com.dlqudtjs.codingbattle.service.game;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;

public interface GameService {

    ResponseDto startGame(GameStartRequestDto requestDto);
}
