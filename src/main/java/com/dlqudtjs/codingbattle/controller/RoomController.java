package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomEnterRequestDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomInfoResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.GameRoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto.GameRoomEnterUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto.GameRoomLeaveUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RoomService gameRoomService;
    private final SocketRoomController socketRoomController;

    @PostMapping("/v1/gameRoom")
    public ResponseEntity<ResponseDto> createRoom(@Valid @RequestBody GameRoomCreateRequestDto requestDto,
                                                  @RequestHeader("Authorization") String token) {
        String userId = jwtTokenProvider.getUserName(token);
        ResponseDto responseDto = gameRoomService.createGameRoom(requestDto, userId);

        // 방안에 사용자들에게 나간 유저의 정보를 전달
        GameRoomInfoResponseDto gameRoomInfoResponseDto = (GameRoomInfoResponseDto) responseDto.getData();
        if (gameRoomInfoResponseDto.getLeaveUserStatus() != null) {
            socketRoomController.sendToRoom(
                    gameRoomInfoResponseDto.getLeaveUserStatus().getRoomId(),
                    GameRoomLeaveUserStatusMessageResponseDto.builder()
                            .leaveUserStatus(gameRoomInfoResponseDto.getLeaveUserStatus())
                            .build()
            );
        }

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping("/v1/gameRoom/enter")
    public ResponseEntity<ResponseDto> enterRoom(@RequestBody GameRoomEnterRequestDto requestDto,
                                                 @RequestHeader("Authorization") String token) {
        String userId = jwtTokenProvider.getUserName(token);
        ResponseDto responseDto = gameRoomService.enterGameRoom(requestDto.getRoomId(), userId);

        // 방안에 사용자들에게 나간 유저의 정보를 전달
        GameRoomInfoResponseDto gameRoomInfoResponseDto = (GameRoomInfoResponseDto) responseDto.getData();
        if (gameRoomInfoResponseDto.getLeaveUserStatus() != null) {
            socketRoomController.sendToRoom(
                    gameRoomInfoResponseDto.getLeaveUserStatus().getRoomId(),
                    GameRoomLeaveUserStatusMessageResponseDto.builder()
                            .leaveUserStatus(gameRoomInfoResponseDto.getLeaveUserStatus())
                            .build()
            );
        }

        // 방안에 사용자들에게 들어온 유저의 정보를 전달
        GameRoomEnterUserStatusMessageResponseDto enterUserStatusResponseDto = GameRoomEnterUserStatusMessageResponseDto.builder()
                .enterUserStatus(GameRoomUserStatusResponseDto.builder()
                        .userId(requestDto.getUserId())
                        .isReady(false)
                        .language(ProgrammingLanguage.DEFAULT.getLanguageName())
                        .build())
                .build();
        socketRoomController.sendToRoom(requestDto.getRoomId(), enterUserStatusResponseDto);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping("/v1/gameRoom/leave/{roomId}")
    public ResponseEntity<ResponseDto> leaveRoom(@PathVariable("roomId") Integer roomId,
                                                 @RequestHeader("Authorization") String token) {
        String userId = jwtTokenProvider.getUserName(token);
        ResponseDto responseDto = gameRoomService.leaveGameRoom(roomId, userId);

        GameRoomLeaveUserStatusResponseDto gameRoomLeaveUserStatusResponseDto =
                (GameRoomLeaveUserStatusResponseDto) responseDto.getData();

        socketRoomController.sendToRoom(
                roomId,
                GameRoomLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(gameRoomLeaveUserStatusResponseDto)
                        .build()
        );

        return ResponseEntity.status(responseDto.getStatus()).body(null);
    }

    @GetMapping("/v1/gameRoomList")
    public ResponseEntity<ResponseDto> getGameRoomList() {
        ResponseDto responseDto = gameRoomService.getGameRoomList();

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }
}
