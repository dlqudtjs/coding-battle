package com.dlqudtjs.codingbattle.controller;

import static com.dlqudtjs.codingbattle.common.constant.Destination.ROOM_BROADCAST;
import static com.dlqudtjs.codingbattle.common.constant.Destination.ROOM_BROADCAST_VALUE;

import com.dlqudtjs.codingbattle.dto.game.responseDto.GameEndResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemsResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.GameEndMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.GameLeaveUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameUserStatusListMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.LeaveGameUserStatus;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.game.GameService;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final SimpMessagingTemplate messagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoomService roomService;
    private final UserService userService;
    private final GameService gameService;

    @GetMapping("/v1/games/{roomId}/problems")
    public ResponseEntity<ProblemsResponseDto> getProblems(@PathVariable("roomId") Long roomId) {
        GameSession gameSession = gameService.getGameSession(roomId);

        return ResponseEntity.status(HttpStatus.OK).body(ProblemsResponseDto.builder()
                .problems(gameSession.getProblemInfo()).build());
    }

    @PostMapping("/v1/games/{roomId}/leave")
    @SendTo(ROOM_BROADCAST_VALUE + "{roomId}")
    public GameLeaveUserStatusMessageResponseDto leaveGame(@PathVariable("roomId") Long roomId,
                                                           @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        LeaveGameUserStatus leaveGameUserStatus = gameService.leaveGame(roomId, user);

        return GameLeaveUserStatusMessageResponseDto.builder()
                .leaveUserStatus(leaveGameUserStatus)
                .build();
    }

    @PostMapping("/v1/games/{roomId}/end")
    @SendTo(ROOM_BROADCAST_VALUE + "{roomId}")
    public GameUserStatusListMessageResponseDto endGame(@PathVariable("roomId") Long roomId,
                                                        @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        Winner winner = gameService.endGame(roomId, user);

        sendGameEndMessage(roomId, winner);

        Room room = roomService.gameEnd(roomId);

        List<RoomUserStatusResponseDto> userStatus = room.getRoomUserStatusList().stream()
                .map(roomUserStatus -> RoomUserStatusResponseDto.builder()
                        .userId(roomUserStatus.getUserId())
                        .isReady(roomUserStatus.getIsReady())
                        .language(roomUserStatus.getUseLanguage().getLanguageName())
                        .build())
                .toList();

        // 방에 초기화된 유저 정보 전송
        return GameUserStatusListMessageResponseDto.builder()
                .userStatusList(userStatus)
                .build();
    }

    public void logout(Long roomId, User user) {
        LeaveGameUserStatus leaveGameUserStatus = gameService.leaveGame(roomId, user);

        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + roomId,
                GameLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(leaveGameUserStatus)
                        .build());
    }

    private void sendGameEndMessage(Long roomId, Winner winner) {
        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + roomId,
                GameEndMessageResponseDto.builder()
                        .gameEnd(GameEndResponseDto.builder()
                                .result(winner.getMatchingResultType())
                                .userId(winner.getUserId())
                                .code(winner.getCode())
                                .language(winner.getLanguage())
                                .build())
                        .build()
        );
    }
}
