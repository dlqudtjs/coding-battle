package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemInfoResponseDto;
import com.dlqudtjs.codingbattle.entity.problem.ProblemInfo;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameSession {

    private Long matchId;
    private final GameRoom gameRoom;
    private Map<String, GameUserStatus> gameUserStatusMap;
    private final List<ProblemInfo> problemInfoList;
    private final Timestamp startTime;

    public GameSession(GameRoom gameRoom, List<ProblemInfo> problemInfoList) {
        this.gameRoom = gameRoom;
        this.problemInfoList = problemInfoList;
        this.startTime = new Timestamp(System.currentTimeMillis());
        initGameUserStatusMap();
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

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
}
