package com.dlqudtjs.codingbattle.entity.game;

import static com.dlqudtjs.codingbattle.common.constant.MatchingResultType.*;
import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemInfoResponseDto;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import com.dlqudtjs.codingbattle.entity.submit.Submit;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

@Getter
public class GameSession {

    private Long matchId;
    private final GameRoom gameRoom;
    private Map<String, GameUserStatus> gameUserStatusMap;
    private final List<ProblemInfo> problemInfoList;
    private final Timestamp startTime;
    private Submit firstCorrectSubmit;
    private Boolean perfectWin;

    public GameSession(GameRoom gameRoom, List<ProblemInfo> problemInfoList) {
        this.gameRoom = gameRoom;
        this.problemInfoList = problemInfoList;
        this.startTime = new Timestamp(System.currentTimeMillis());
        initGameUserStatusMap();
    }

    public Winner endGame() {
        if (!canEndGame()) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        Winner winner = getWinner();

        initGameUserStatusMap();

        return null;
    }

    private Winner getWinner() {
        // 혼자 남았을 때
        if (isAlone()) {
            GameUserStatus gameUserStatus = gameUserStatusMap.values().stream().findFirst().orElseThrow(
                    () -> new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus())
            );

            return new Winner(gameUserStatus.getUser(), PERFECT_WIN, null);
        }

        // 문제를 맞춘 사람이 있을 경우
        if (firstCorrectSubmit != null) {
            MatchingResultType matchingResultType = perfectWin ? PERFECT_WIN : WIN;
            new Winner(firstCorrectSubmit.getUser(), matchingResultType, firstCorrectSubmit.getCode());
        }

        // 시간 초과 및 `다 풀었어요!`버튼을 다 눌렀지만 맞춘 사람이 없는 경우
        return null;
    }

    private Boolean canEndGame() {
        return isAlone() ||
                isTimeOver() ||
                isAllUserSubmitDone();
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public void reflectSubmit(Submit submit) {
        if (firstCorrectSubmit == null) {
            this.firstCorrectSubmit = submit;
            perfectWin = true;
            return;
        }

        // 처음 제출한 코드의 실행 시간보다 늦게 제출한 코드의 실행 시간이 더 빠른 경우
        if (firstCorrectSubmit.getExecutionTime() < submit.getExecutionTime()) {
            perfectWin = false;
        }
    }

    private void initGameUserStatusMap() {
        gameUserStatusMap = new ConcurrentHashMap<>();

        gameRoom.getUserList().forEach(user -> {
            gameUserStatusMap.put(user.getUserId(), GameUserStatus.builder()
                    .user(user)
                    .isSubmitDone(false)
                    .build());
        });
    }

    public Boolean toggleSubmitDone(String userId) {
        GameUserStatus gameUserStatus = gameUserStatusMap.get(userId);
        return gameUserStatus.toggleSubmitDone();
    }


    // 방에 혼자 남았는지 확인
    private Boolean isAlone() {
        return gameUserStatusMap.size() == 1;
    }

    // 시간 초과 확인
    private Boolean isTimeOver() {
        // 밀리초 변환
        long limitTime = gameRoom.getLimitTime() * 60 * 1000;
        return System.currentTimeMillis() - startTime.getTime() > limitTime;
    }

    // 모든 유저가 `다 풀었어요!` 버튼을 눌렀는지 확인
    private Boolean isAllUserSubmitDone() {
        return gameUserStatusMap.values().stream()
                .allMatch(GameUserStatus::getIsSubmitDone);
    }

    public List<ProblemInfoResponseDto> getProblemInfo() {
        List<ProblemInfoResponseDto> infoResponseDtoList = new ArrayList<>();

        for (ProblemInfo problemInfo : problemInfoList) {
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
