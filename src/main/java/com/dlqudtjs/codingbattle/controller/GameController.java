package com.dlqudtjs.codingbattle.controller;

import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.game.responseDto.GameEndResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.ProblemsResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.UserSurrenderResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.GameEndMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.GameLeaveUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.UserSurrenderMessageResponseDto;
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
    public ResponseEntity<ProblemsResponseDto> getProblems(@PathVariable("roomId") Long roomId,
                                                           @RequestHeader("Authorization") String token) {
        GameSession gameSession = gameService.getGameSession(roomId);

        return ResponseEntity.status(HttpStatus.OK).body(ProblemsResponseDto.builder()
                .problems(gameSession.getProblemInfo()).build());
    }

    @PostMapping("/v1/games/{roomId}/leave")
    public ResponseEntity<ResponseDto> leaveGame(@PathVariable("roomId") Long roomId,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        LeaveGameUserStatus leaveGameUserStatus = gameService.leaveGame(roomId, user);

        messagingTemplate.convertAndSend("/topic/rooms/" + roomId,
                GameLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(leaveGameUserStatus)
                        .build());

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/v1/games/{roomId}/end")
    public ResponseEntity<ResponseDto> endGame(@PathVariable("roomId") Long roomId,
                                               @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        Winner winner = gameService.endGame(roomId, user);

        GameEndResponseDto gameEndResponseDto = GameEndResponseDto.builder()
                .result(winner.getMatchingResultType())
                .userId(winner.getUserId())
                .code(winner.getCode())
                .language(winner.getLanguage())
                .build();

        // 방에 Winner 전송
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId,
                GameEndMessageResponseDto.builder()
                        .gameEnd(gameEndResponseDto)
                        .build());

        Room room = roomService.gameEnd(roomId);

        List<RoomUserStatusResponseDto> userStatus = room.getRoomUserStatusList().stream()
                .map(roomUserStatus -> RoomUserStatusResponseDto.builder()
                        .userId(roomUserStatus.getUserId())
                        .isReady(roomUserStatus.getIsReady())
                        .language(roomUserStatus.getUseLanguage().getLanguageName())
                        .build())
                .toList();

        // 방에 초기화된 유저 정보 전송
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId,
                GameUserStatusListMessageResponseDto.builder()
                        .userStatusList(userStatus)
                        .build());

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/v1/games/{roomId}/{userId}/surrender")
    public ResponseEntity<ResponseDto> surrender(@PathVariable("roomId") Long roomId,
                                                 @PathVariable("userId") String userId,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));
        if (!user.getUserId().equals(userId)) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        User surrenderUser = gameService.surrender(
                roomId,
                userService.getUser(jwtTokenProvider.getUserName(token)));

        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                UserSurrenderMessageResponseDto.builder()
                        .userSurrender(UserSurrenderResponseDto.builder()
                                .userId(surrenderUser.getUserId())
                                .surrender(true)
                                .build())
                        .build());

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
