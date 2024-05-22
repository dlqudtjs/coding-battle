package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemResponseDto;
import com.dlqudtjs.codingbattle.entity.problem.Problem;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameSession {

    private GameRoom gameRoom;
    private List<Problem> problemList;

    public List<ProblemResponseDto> getProblemResponseList() {
        return problemList.stream()
                .map(Problem::toResponseDto)
                .toList();

    }
}
