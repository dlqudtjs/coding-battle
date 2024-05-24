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

    private GameRoom gameRoom;
    private List<ProblemInfo> problemInfoList;
    private Timestamp startTime;

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
