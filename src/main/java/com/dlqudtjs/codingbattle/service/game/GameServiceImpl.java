package com.dlqudtjs.codingbattle.service.game;

import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.LeaveGameUserStatus;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.service.match.MatchService;
import com.dlqudtjs.codingbattle.service.problem.ProblemService;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final static Map<Long, GameSession> gameSessionMap = new ConcurrentHashMap<>();
    private final ProblemService problemService;
    private final MatchService matchService;
    private final RoomService roomService;

    @Override
    public GameSession startGame(Long roomId, User user) {
        Room room = roomService.getRoom(roomId);
        if (room.isStarted()) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // Room의 상태를 게임 시작으로 변경
        if (!room.startGame()) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        // 난이도에 따른 문제 리스트 가져오기
        ProblemLevel problemLevel = room.getGameRunningConfig().getProblemLevel();
        List<ProblemInfo> problemInfoList = problemService.getProblemInfoList(problemLevel, 1);

        GameSession gameSession = new GameSession(room, problemInfoList, matchService, roomService);

        gameSessionMap.put(roomId, gameSession);

        return gameSession;
    }

    @Override
    public LeaveGameUserStatus leaveGame(Long roomId, User user) {
        // 나가려는 방이 존재하지 않을 때
        if (!gameSessionMap.containsKey(roomId)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        GameSession gameSession = gameSessionMap.get(roomId);

        gameSession.leaveGame(user);

        return LeaveGameUserStatus.builder()
                .roomId(roomId)
                .userId(user.getUserId())
                .build();
    }

    @Override
    public Winner endGame(Long roomId) {
        GameSession gameSession = gameSessionMap.get(roomId);

        Winner winner = gameSession.endGame();

        // 매치 기록 저장
        matchService.saveUserMatchHistory(gameSession, winner);
        gameSessionMap.remove(roomId);

        return winner;
    }

    @Override
    public User surrender(Long roomId, User user) {
        GameSession gameSession = gameSessionMap.get(roomId);

        if (gameSession == null) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        if (!gameSession.existUser(user)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return gameSession.surrender(user);
    }

    @Override
    public GameSession getGameSession(Long roomId) {
        if (!gameSessionMap.containsKey(roomId)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return gameSessionMap.get(roomId);
    }
}
