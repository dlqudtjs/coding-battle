package com.dlqudtjs.codingbattle.service.game;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.MatchHistory;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import com.dlqudtjs.codingbattle.service.match.MatchService;
import com.dlqudtjs.codingbattle.service.problem.ProblemService;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    static Map<Long, GameSession> gameSessionMap = new ConcurrentHashMap<>();
    private final RoomService roomService;
    private final ProblemService problemService;
    private final MatchService matchService;

    @Override
    public GameSession startGame(GameStartRequestDto requestDto) {
        // 게임 시작
        GameRoom gameRoom = roomService.startGame(requestDto.getRoomId());

        // 난이도에 따른 문제 리스트 가져오기
        ProblemLevelType problemLevel = gameRoom.getProblemLevel();
        List<ProblemInfo> problemInfoList = problemService.getProblemInfoList(null, problemLevel, 1);

        GameSession gameSession = GameSession.builder()
                .gameRoom(gameRoom)
                .problemInfoList(problemInfoList)
                .startTime(new Timestamp(System.currentTimeMillis()))
                .build();

        gameSessionMap.put(requestDto.getRoomId(), gameSession);

        // 매치 기록 저장
        MatchHistory matchHistory = matchService.startMatch(gameSession);
        gameSession.setMatchId(matchHistory.getId());

        return gameSession;
    }

    @Override
    public List<ProblemInfo> getProblemInfoList(Long roomId) {
        return gameSessionMap.get(roomId).getProblemInfoList();
    }
}
