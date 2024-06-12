package com.dlqudtjs.codingbattle.controller;

import static com.dlqudtjs.codingbattle.common.constant.Destination.ROOM_BROADCAST;
import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.JudgeResultCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.game.requestDto.JudgeResultRequestDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.JudgeResultMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.judge.JudgeProblemRequestDto;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.judge.JudgeService;
import com.dlqudtjs.codingbattle.service.submit.SubmitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequiredArgsConstructor
@Log4j2
public class JudgeController {

    @Value("${docker.secret.key}")
    private String secretKey;

    private final JudgeService judgeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SubmitService submitService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/v1/judges")
    public ResponseEntity<ResponseDto> judge(@Valid @RequestBody JudgeProblemRequestDto judgeProblemRequestDto,
                                             @RequestHeader("Authorization") String token) {
        if (!jwtTokenProvider.getUserName(token).equals(judgeProblemRequestDto.getUserId())) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        ResponseDto responseDto = judgeService.judgeProblem(judgeProblemRequestDto);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping("/v1/judges/results")
    public ResponseEntity<ResponseDto> judgeResults(@RequestBody JudgeResultRequestDto JudgeResultRequestDto) {

        // secret key 검증
        if (!JudgeResultRequestDto.getSecretKey().equals(secretKey)) {
            log.error("JudgeResults - secret key is not matched");
            return ResponseEntity.badRequest().build();
        }

        log.info("userId : " + JudgeResultRequestDto.getUserId() + " / " +
                "result : " + JudgeResultRequestDto.getResult());

        JudgeResultMessageResponseDto responseDto = JudgeResultMessageResponseDto.builder()
                .judgeResult(JudgeResultRequestDto.toParsedJudgeResultResponseDto())
                .build();

        boolean isFinished = isFinished(JudgeResultRequestDto);

        // 결과 저장 (마지막 테스트까지 통과, Fail, Error)
        if (isFinished) {
            submitService.saveSubmitResult(JudgeResultRequestDto.toParsedJudgeResultResponseDto());
        }

        // 결과 전송
        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + JudgeResultRequestDto.getRoomId(), responseDto);

        if (isFinished) {
            judgeService.closeDockerContainer(JudgeResultRequestDto.getContainerId());
        }

        return ResponseEntity.ok().build();
    }

    private Boolean isFinished(JudgeResultRequestDto result) {
        return !result.getResult().equals(JudgeResultCode.PASS) ||
                result.getCurrentTest().equals(result.getTotalTests());
    }
}
