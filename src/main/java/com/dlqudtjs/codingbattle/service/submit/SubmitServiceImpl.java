package com.dlqudtjs.codingbattle.service.submit;

import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.SubmitResultManager;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.util.Time;
import com.dlqudtjs.codingbattle.dto.game.requestDto.UpdateSubmitResultRequestDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ParsedJudgeResultResponseDto;
import com.dlqudtjs.codingbattle.dto.judge.JudgeProblemRequestDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.submit.Submit;
import com.dlqudtjs.codingbattle.entity.submit.SubmitResult;
import com.dlqudtjs.codingbattle.repository.game.SubmitRepository;
import com.dlqudtjs.codingbattle.repository.game.SubmitResultRepository;
import com.dlqudtjs.codingbattle.service.game.GameService;
import com.dlqudtjs.codingbattle.service.match.MatchService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmitServiceImpl implements SubmitService {

    private final SubmitResultRepository submitResultRepository;
    private final SubmitRepository submitRepository;
    private final GameService gameService;
    private final UserService userService;
    private final MatchService matchService;


    @Override
    public void saveSubmitResult(ParsedJudgeResultResponseDto judgeResultResponseDto) {
        Submit submit = submitRepository.findById(judgeResultResponseDto.getSubmitId()).orElseThrow(
                () -> new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus()));

        SubmitResult submitResult = judgeResultResponseDto.getResult();

        submit.updateSubmitResult(UpdateSubmitResultRequestDto.builder()
                .executionTime(judgeResultResponseDto.getExecutionTime())
                .submitResult(submitResult)
                .build());

        gameService.getGameSession(judgeResultResponseDto.getRoomId()).reflectSubmit(submit);
    }

    @Override
    public Submit savedSubmit(JudgeProblemRequestDto judgeProblemRequestDto) {
        GameSession gameSession = gameService.getGameSession(judgeProblemRequestDto.getRoomId());

        return submitRepository.save(Submit.builder()
                .user(userService.getUserInfo(judgeProblemRequestDto.getUserId()).getUser())
                .submitResult(SubmitResultManager.PENDING)
                .matchHistory(matchService.getMatchHistory(gameSession.getMatchId()))
                .code(judgeProblemRequestDto.getCode())
                .memory(0L)
                .executionTime(0L)
                .language(judgeProblemRequestDto.getProgrammingLanguage())
                .submitTime(Time.getDate())
                .build());
    }
}
