package com.dlqudtjs.codingbattle.service.game;

import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemInfoResponseDto;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import java.util.List;

public interface GameService {

    List<ProblemInfoResponseDto> startGame(GameStartRequestDto requestDto);

    List<ProblemInfo> getProblemInfoList(Long roomId);
}
