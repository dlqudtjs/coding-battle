package com.dlqudtjs.codingbattle.service.game;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameEndRequestDto;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.MatchHistory;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.service.match.MatchService;
import com.dlqudtjs.codingbattle.service.problem.ProblemService;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.user.UserService;
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
    private final UserService userService;
    private final ProblemService problemService;
    private final MatchService matchService;

    @Override
    public GameSession startGame(GameStartRequestDto requestDto) {
        // 게임 시작
        GameRoom gameRoom = roomService.startGame(requestDto.getRoomId());

        // 난이도에 따른 문제 리스트 가져오기
        ProblemLevelType problemLevel = gameRoom.getProblemLevel();
        List<ProblemInfo> problemInfoList = problemService.getProblemInfoList(null, problemLevel, 1);

        GameSession gameSession = new GameSession(gameRoom, problemInfoList);

        gameSessionMap.put(requestDto.getRoomId(), gameSession);

        // 매치 기록 저장
        MatchHistory matchHistory = matchService.startMatch(gameSession);
        gameSession.setMatchId(matchHistory.getId());

        return gameSession;
    }

    @Override
    public Winner endGame(GameEndRequestDto requestDto) {
        GameSession gameSession = gameSessionMap.get(requestDto.getRoomId());

        canEndGame(gameSession);

        getWinner(requestDto.getRoomId());

        UserInfo userInfo = userService.getUserInfo("a");
        Winner winner = new Winner(userInfo.getUser(), "");

        return null;
    }

    @Override
    public Boolean toggleSubmitDone(Long roomId, String userId) {
        GameSession gameSession = gameSessionMap.get(roomId);
        return gameSession.toggleSubmitDone(userId);
    }

    @Override
    public GameRoom initGameRoom(Long roomId) {
        // TODO: 게임 초기화 유저 정보를 토기화해서 주기 + 게임 세션 삭제

        return null;
    }

    @Override
    public GameSession getGameSession(Long roomId) {
        if (!gameSessionMap.containsKey(roomId)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return gameSessionMap.get(roomId);
    }

    @Override
    public List<ProblemInfo> getProblemInfoList(Long roomId) {
        return gameSessionMap.get(roomId).getProblemInfoList();
    }

    private Winner getWinner(Long roomId) {
        // TODO: 승자 결정
        return null;
    }

    private Boolean canEndGame(GameSession gameSession) {
        // TODO: 게임 종료 조건이 됐는지 확인
        // 방에 혼자 남았는지
        // 시간이 초과됐는지
        // 모든 유저가 다 풀었어요! 버튼을 눌렀는지

        gameSession.getGameRoom();

        return true;
    }
}
