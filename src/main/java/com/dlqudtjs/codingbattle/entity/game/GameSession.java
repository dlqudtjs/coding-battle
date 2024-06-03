package com.dlqudtjs.codingbattle.entity.game;

import static com.dlqudtjs.codingbattle.common.constant.MatchingResultType.DRAW;
import static com.dlqudtjs.codingbattle.common.constant.MatchingResultType.WIN;
import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.JudgeResultCode;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemInfoResponseDto;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.submit.Submit;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.service.match.MatchService;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public class GameSession {
    private final Long matchId;
    private final Long startTime;
    private final GameRunningConfig gameRunningConfig;
    private final MatchService matchService;
    private final RoomService roomService;
    private final Queue<Submit> submitQueue;
    private final Map<User, GameUserStatus> gameUserStatusMap = new ConcurrentHashMap<>();

    public GameSession(Room room,
                       List<ProblemInfo> problemInfoList,
                       MatchService matchService,
                       RoomService roomService) {
        initGameUserStatusMap(room);

        this.gameRunningConfig = room.getGameRunningConfig();
        this.startTime = System.currentTimeMillis();
        this.matchService = matchService;
        this.roomService = roomService;
        gameRunningConfig.setProblemInfoList(problemInfoList);
        this.matchId = saveMatch().getId();
        submitQueue = new PriorityQueue<>();
    }

    public Winner endGame(User user) {
        // 방장이 아니면 게임 종료 불가
        if (!roomService.getRoom(gameRunningConfig.getRoomId()).isHost(user))) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        if (!canEndGame()) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        return getWinner();
    }

    public User leaveGame(User user) {
        if (!gameUserStatusMap.containsKey(user)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        roomService.leave(gameRunningConfig.getRoomId(), user);
        gameUserStatusMap.remove(user);
        return user;
    }

    private Winner getWinner() {
        while (!submitQueue.isEmpty()) {
            Submit submit = submitQueue.poll();

            if (gameUserStatusMap.containsKey(submit.getUser())) {
                return new Winner(submit.getUser(), WIN, submit.getCode());
            }
        }

        return new Winner(null, DRAW, null);
    }

    public List<User> getGameUserList() {
        return gameUserStatusMap.values().stream()
                .map(GameUserStatus::getUser)
                .toList();
    }

    private MatchHistory saveMatch() {
        return matchService.startMatch(this);
    }

    private Boolean canEndGame() {
        return isAlone() ||
                isTimeOver() ||
                isAllUserSubmitDone();
    }

    public void reflectSubmit(Submit submit) {
        if (JudgeResultCode.valueOf(submit.getSubmitResultCode().getName()) == JudgeResultCode.PASS) {
            submitQueue.add(submit);
        }
    }

    private void initGameUserStatusMap(Room room) {
        room.getUserList().forEach(user -> {
            gameUserStatusMap.put(user, GameUserStatus.builder()
                    .user(user)
                    .isSubmitDone(false)
                    .build());
        });
    }

    public Boolean toggleSubmitDone(User user) {
        GameUserStatus gameUserStatus = gameUserStatusMap.get(user);
        return gameUserStatus.toggleSubmitDone();
    }


    // 방에 혼자 남았는지 확인
    private Boolean isAlone() {
        return gameUserStatusMap.size() == 1;
    }

    // 시간 초과 확인
    private Boolean isTimeOver() {
        // 밀리초 변환
        long limitTime = gameRunningConfig.getLimitTime() * 60 * 1000;
        return System.currentTimeMillis() - startTime > limitTime;
    }

    // 모든 유저가 `다 풀었어요!` 버튼을 눌렀는지 확인
    private Boolean isAllUserSubmitDone() {
        return gameUserStatusMap.values().stream()
                .allMatch(GameUserStatus::getIsSubmitDone);
    }

    public List<ProblemInfoResponseDto> getProblemInfo() {
        List<ProblemInfoResponseDto> infoResponseDtoList = new ArrayList<>();

        for (ProblemInfo problemInfo : gameRunningConfig.getProblemInfoList()) {
            infoResponseDtoList.add(ProblemInfoResponseDto.builder()
                    .id(problemInfo.getProblem().getId())
                    .algorithmClassification(problemInfo.getProblem().getAlgorithmClassification().getName())
                    .problemLevel(problemInfo.getProblem().getProblemLevel().getName())
                    .title(problemInfo.getProblem().getTitle())
                    .problemDescription(problemInfo.getProblem().getProblemDescription())
                    .inputDescription(problemInfo.getProblem().getInputDescription())
                    .outputDescription(problemInfo.getProblem().getOutputDescription())
                    .hint(problemInfo.getProblem().getHint())
                    .problemIOExamples(problemInfo.getProblemIOExamples())
                    .build());
        }

        return infoResponseDtoList;
    }
}
