package com.dlqudtjs.codingbattle.service.game;

import com.dlqudtjs.codingbattle.dto.game.requestDto.GameEndRequestDto;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import java.util.List;

public interface GameService {

    GameSession startGame(GameStartRequestDto requestDto);

    Winner endGame(GameEndRequestDto requestDto);

    GameRoom initGameRoom(Long roomId);

    GameSession getGameSession(Long roomId);

    List<ProblemInfo> getProblemInfoList(Long roomId);
}
