package com.dlqudtjs.codingbattle.service.judge;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.judge.JudgeProblemRequestDto;

public interface JudgeService {

    ResponseDto judgeProblem(JudgeProblemRequestDto judgeProblemRequestDto);
}
