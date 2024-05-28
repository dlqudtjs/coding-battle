package com.dlqudtjs.codingbattle.entity.game;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemInfoResponseDto;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
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

    public void setFirstCorrectSubmit(Submit submit) {
        if (firstCorrectSubmit != null) {
            return;
        }

        this.firstCorrectSubmit = submit;
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
