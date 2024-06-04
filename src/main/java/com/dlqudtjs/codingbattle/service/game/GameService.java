package com.dlqudtjs.codingbattle.service.game;

import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.LeaveGameUserStatus;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.user.User;
import java.util.List;

public interface GameService {

    GameSession startGame(Long roomId, User user);

    LeaveGameUserStatus leaveGame(Long roomId, User user);

    Winner endGame(Long roomId, User user);

    User surrender(Long roomId, User user);

    GameSession getGameSession(Long roomId);

    List<ProblemInfo> getProblemInfoList(Long roomId);
}
