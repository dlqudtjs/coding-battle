package com.dlqudtjs.codingbattle.service.game;

import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.room.LeaveUserStatus;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import java.util.List;

public interface GameService {

    GameSession startGame(Long roomId, User user);

    LeaveUserStatus leaveGame(Long roomId, User user);

    Winner endGame(Long roomId, User user);

    Room resetRoom(Long roomId);

    Boolean toggleSubmitDone(Long roomId, User user);

    GameSession getGameSession(Long roomId);

    List<ProblemInfo> getProblemInfoList(Long roomId);
}
