package com.dlqudtjs.codingbattle.service.game;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.entity.problem.Problem;
import java.util.List;

public interface GameService {

    ResponseDto startGame(GameStartRequestDto requestDto);

    List<Problem> getProblemList(Long roomId);
}
