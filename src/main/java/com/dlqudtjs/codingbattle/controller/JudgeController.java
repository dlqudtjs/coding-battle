package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.game.requestDto.JudgeResultRequestDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.JudgeResultMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.judge.JudgeProblemRequestDto;
import com.dlqudtjs.codingbattle.service.judge.JudgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@Log4j2
public class JudgeController {

    @Value("${docker.secret.key}")
    private String secretKey;

    private final JudgeService judgeService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/v1/judge")
    public ResponseEntity<ResponseDto> judge(@Valid @RequestBody JudgeProblemRequestDto judgeProblemRequestDto) {
        ResponseDto responseDto = judgeService.judgeProblem(judgeProblemRequestDto);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping("/v1/judge/results")
    public ResponseEntity<ResponseDto> judgeResults(@RequestBody JudgeResultRequestDto JudgeResultRequestDto) {

        // secret key 검증
        if (!JudgeResultRequestDto.getSecretKey().equals(secretKey)) {
            log.error("JudgeResults - secret key is not matched");
            return ResponseEntity.badRequest().build();
        }

        JudgeResultMessageResponseDto responseDto = JudgeResultMessageResponseDto.builder()
                .judgeResult(JudgeResultRequestDto.toJudgeResultResponseDto())
                .build();

        log.info("JudgeUserResults : " + responseDto.getJudgeResult() +
                ", JudgeResult : " + JudgeResultRequestDto.getResult());

        // 결과 전송
        messagingTemplate.convertAndSend("/topic/room/" + JudgeResultRequestDto.getRoomId(), responseDto);

        // 결과 저장 (마지막 테스트까지 통과, Fail, Error)

        return ResponseEntity.ok().build();
    }
}
