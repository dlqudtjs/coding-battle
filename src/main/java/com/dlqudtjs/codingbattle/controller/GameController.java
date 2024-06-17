package com.dlqudtjs.codingbattle.controller;

import static com.dlqudtjs.codingbattle.common.constant.Destination.ROOM_BROADCAST;

import com.dlqudtjs.codingbattle.common.constant.code.RoomConfigCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
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
    public ResponseEntity<ResponseDto> leaveGame(@PathVariable("roomId") Long roomId,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        LeaveGameUserStatus leaveGameUserStatus = gameService.leaveGame(roomId, user);
        sendLeaveGameMessage(roomId, leaveGameUserStatus);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                .message(RoomConfigCode.LEAVE_GAME_SUCCESS.getMessage())
                .status(RoomConfigCode.LEAVE_GAME_SUCCESS.getStatus().value())
                .data(null)
                .build());
    }

    @PostMapping("/v1/games/{roomId}/end")
    public ResponseEntity<ResponseDto> endGame(@PathVariable("roomId") Long roomId,
                                               @RequestHeader("Authorization") String token) {
        Winner winner = gameService.endGame(roomId);

        // 게임 종료 시 메시지 전송
        sendGameEndMessage(roomId, winner);

        Room room = roomService.gameEnd(roomId);

        // 방에 초기화된 유저 정보 전송
        sendInitUserStatus(room);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                .message(RoomConfigCode.END_GAME_SUCCESS.getMessage())
                .status(RoomConfigCode.END_GAME_SUCCESS.getStatus().value())
                .data(null)
                .build());
    }

    public void logout(Long roomId, User user) {
        LeaveGameUserStatus leaveGameUserStatus = gameService.leaveGame(roomId, user);

        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + roomId,
                GameLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(leaveGameUserStatus)
                        .build());
    }

    private void sendLeaveGameMessage(Long roomId, LeaveGameUserStatus leaveGameUserStatus) {
        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + roomId,
                GameLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(leaveGameUserStatus)
                        .build());
    }

    private void sendGameEndMessage(Long roomId, Winner winner) {
        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + roomId,
                GameEndMessageResponseDto.builder()
                        .gameEnd(GameEndResponseDto.builder()
                                .result(winner.getMatchResult())
                                .userId(winner.getUserId())
                                .code(winner.getCode())
                                .language(winner.getLanguage())
                                .build())
                        .build()
        );
    }

    private void sendInitUserStatus(Room room) {
        List<RoomUserStatusResponseDto> userStatus = room.getRoomUserStatusList().stream()
                .map(roomUserStatus -> RoomUserStatusResponseDto.builder()
                        .userId(roomUserStatus.getUserId())
                        .isReady(roomUserStatus.getIsReady())
                        .language(roomUserStatus.getUseLanguage())
                        .build())
                .toList();

        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + room.getRoomId(),
                GameUserStatusListMessageResponseDto.builder()
                        .userStatusList(userStatus)
                        .build());
    }
}
