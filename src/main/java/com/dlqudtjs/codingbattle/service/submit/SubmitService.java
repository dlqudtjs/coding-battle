package com.dlqudtjs.codingbattle.service.submit;

import com.dlqudtjs.codingbattle.dto.game.responseDto.ParsedJudgeResultResponseDto;
import com.dlqudtjs.codingbattle.dto.judge.JudgeProblemRequestDto;
import com.dlqudtjs.codingbattle.entity.submit.Submit;

public interface SubmitService {

    Submit savedSubmit(JudgeProblemRequestDto judgeProblemRequestDto);

    void saveSubmitResult(ParsedJudgeResultResponseDto judgeResultResponseDto);
}
