package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.GameEndResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemInfoResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.StartGameResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.GameEndMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomUserStatusListMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.game.GameService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final GameService gameService;

    @PostMapping("/v1/game/{roomId}/start")
    public ResponseEntity<ResponseDto> startGame(@PathVariable("roomId") Long roomId,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        GameSession gameSession = gameService.startGame(roomId, user);
        List<ProblemInfoResponseDto> infoResponseDtoList = gameSession.getProblemInfo();

        StartGameResponseDto startGameResponseDto = StartGameResponseDto.builder()
                .matchId(gameSession.getMatchId())
                .gameStartInfo(infoResponseDtoList)
                .build();

        // 방에 게임 문제 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                startGameResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/v1/game/{roomId}/leave")
    public ResponseEntity<ResponseDto> leaveGame(@PathVariable("roomId") Long roomId,
                                                 @RequestHeader("Authorization") String token) {
        User user = gameService.leaveGame(roomId, userService.getUser(jwtTokenProvider.getUserName(token)));

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/v1/game/{roomId}/end")
    public ResponseEntity<ResponseDto> endGame(@PathVariable("roomId") Long roomId,
                                               @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));
        Winner winner = gameService.endGame(roomId, user);

        GameEndResponseDto gameEndResponseDto = GameEndResponseDto.builder()
                .userId(winner.getUserId())
                .code(winner.getCode())
                .build();

        // 방에 Winner 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                GameEndMessageResponseDto.builder()
                        .gameEnd(gameEndResponseDto)
                        .build());

        Room room = gameService.resetRoom(roomId);

        GameRoomUserStatusListMessageResponseDto gameRoomUserStatusListMessageResponseDto =
                GameRoomUserStatusListMessageResponseDto.builder()
                        .userStatusList(room.toGameRoomUserStatusResponseDto())
                        .build();

        // 방에 초기화된 유저 정보 전송
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                gameRoomUserStatusListMessageResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
