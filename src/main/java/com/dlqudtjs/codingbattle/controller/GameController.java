package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.constant.code.GameSuccessCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemInfoResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.StartGameResponseDto;
import com.dlqudtjs.codingbattle.service.game.GameService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService;

    @PostMapping("/v1/game/start")
    public ResponseEntity<ResponseDto> startGame(@Valid @RequestBody GameStartRequestDto requestDto) {
        List<ProblemInfoResponseDto> infoResponseDtoList = gameService.startGame(requestDto);

        StartGameResponseDto startGameResponseDto = StartGameResponseDto.builder()
                .problemInfoList(infoResponseDtoList)
                .build();

        // 방에 게임 시작 메시지 전송
        messagingTemplate.convertAndSend("/topic/room/" + requestDto.getRoomId(),
                GameSuccessCode.GAME_END_SUCCESS.getMessage());
        // 방에 게임 문제 전송
        messagingTemplate.convertAndSend("/topic/room/" + requestDto.getRoomId(),
                startGameResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
