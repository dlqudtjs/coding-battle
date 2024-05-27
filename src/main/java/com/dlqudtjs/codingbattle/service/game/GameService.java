package com.dlqudtjs.codingbattle.service.game;

import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import java.util.List;

public interface GameService {

    GameSession startGame(GameStartRequestDto requestDto);

    Boolean isValidMatchIdForRoom(Long roomId, Long matchId);

    List<ProblemInfo> getProblemInfoList(Long roomId);

}
