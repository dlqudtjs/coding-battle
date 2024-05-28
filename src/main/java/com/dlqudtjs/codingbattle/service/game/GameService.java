package com.dlqudtjs.codingbattle.service.game;

import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import java.util.List;

public interface GameService {

    GameSession startGame(GameStartRequestDto requestDto, String requestUserId);

    Winner endGame(Long roomId, String requestUserId);

    GameRoom resetRoom(Long roomId);

    Boolean toggleSubmitDone(Long roomId, String userId);

    GameSession getGameSession(Long roomId);

    List<ProblemInfo> getProblemInfoList(Long roomId);
}
