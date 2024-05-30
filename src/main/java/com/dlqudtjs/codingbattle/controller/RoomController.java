package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.constant.code.RoomConfigCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.GameRoomListResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomEnterUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.GameRoomLeaveUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.LeaveRoomUserStatus;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserSetting;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final RoomService roomService;
    private final SessionService sessionService;
    private final UserService userService;
    private final SocketRoomController socketRoomController;

    @PostMapping("/v1/room")
    public ResponseEntity<ResponseDto> createRoom(@Valid @RequestBody RoomCreateRequestDto requestDto,
                                                  @RequestHeader("Authorization") String token) {
        requestDto.validate();
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        LeaveRoomUserStatus leaveRoomUserStatus = alreadyLeaveRoom(user);

        // 방안에 사용자들에게 나간 유저의 정보를 전달
        socketRoomController.sendToRoom(
                leaveRoomUserStatus.getRoomId(),
                GameRoomLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(RoomLeaveUserStatusResponseDto.builder()
                                .roomId(leaveRoomUserStatus.getRoomId())
                                .userId(leaveRoomUserStatus.getUser().getUserId())
                                .isHost(leaveRoomUserStatus.getIsHost())
                                .build())
                        .build()
        );

        Room room = roomService.create(requestDto, user);
        if (room == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.builder()
                    .message(RoomConfigCode.CREATE_GAME_ROOM_FAIL.getMessage())
                    .status(RoomConfigCode.CREATE_GAME_ROOM_FAIL.getStatus())
                    .data(null)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                .message(RoomConfigCode.CREATE_GAME_ROOM_SUCCESS.getMessage())
                .status(RoomConfigCode.CREATE_GAME_ROOM_SUCCESS.getStatus())
                .data(null)
                .build());
    }

    @PostMapping("/v1/room/enter")
    public ResponseEntity<ResponseDto> enterRoom(@Valid @RequestBody RoomEnterRequestDto requestDto,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));
        UserSetting userSetting = userService.getUserSetting(user);

        LeaveRoomUserStatus leaveRoomUserStatus = alreadyLeaveRoom(user);

        // 방안에 사용자들에게 나간 유저의 정보를 전달
        socketRoomController.sendToRoom(
                leaveRoomUserStatus.getRoomId(),
                GameRoomLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(RoomLeaveUserStatusResponseDto.builder()
                                .roomId(leaveRoomUserStatus.getRoomId())
                                .userId(leaveRoomUserStatus.getUser().getUserId())
                                .isHost(leaveRoomUserStatus.getIsHost())
                                .build())
                        .build()
        );

        Room enterdRoom = roomService.enter(requestDto);
        if (enterdRoom == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.builder()
                    .message(RoomConfigCode.PASSWORD_NOT_MATCH.getMessage())
                    .status(RoomConfigCode.PASSWORD_NOT_MATCH.getStatus())
                    .data(null)
                    .build());
        }

        // 방안에 사용자들에게 들어온 유저의 정보를 전달
        GameRoomEnterUserStatusMessageResponseDto enterUserStatusResponseDto = GameRoomEnterUserStatusMessageResponseDto.builder()
                .enterUserStatus(RoomUserStatusResponseDto.builder()
                        .userId(requestDto.getUserId())
                        .isReady(false)
                        .language(userSetting.getLanguage().getName())
                        .build())
                .build();
        socketRoomController.sendToRoom(requestDto.getRoomId(), enterUserStatusResponseDto);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                .message(RoomConfigCode.JOIN_GAME_ROOM_SUCCESS.getMessage())
                .status(RoomConfigCode.JOIN_GAME_ROOM_SUCCESS.getStatus())
                .data(null)
                .build());
    }

    @PostMapping("/v1/room/leave/{roomId}")
    public ResponseEntity<ResponseDto> leaveRoom(@PathVariable("roomId") Long roomId,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        LeaveRoomUserStatus leaveRoomUserStatus = roomService.leave(roomId, user);

        socketRoomController.sendToRoom(
                roomId,
                GameRoomLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(RoomLeaveUserStatusResponseDto.builder()
                                .roomId(leaveRoomUserStatus.getRoomId())
                                .userId(leaveRoomUserStatus.getUser().getUserId())
                                .isHost(leaveRoomUserStatus.getIsHost())
                                .build())
                        .build()
        );

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/v1/roomList")
    public ResponseEntity<ResponseDto> getGameRoomList() {
        List<Room> roomList = roomService.getRoomList();

        List<GameRoomListResponseDto> responseDtoList = roomList.stream()
                .map(room -> GameRoomListResponseDto.builder()
                        .roomId(room.getRoomId())
                        .hostId(room.getHost().getUserId())
                        .title(room.getTitle())
                        .language(room.getGameRunningConfig().getLanguage().getLanguageName())
                        .isLocked(room.isLocked())
                        .isStarted(room.getIsStarted())
                        .problemLevel(room.getGameRunningConfig().getProblemLevel())
                        .maxUserCount(room.getMaxUserCount())
                        .maxSubmitCount(room.getGameRunningConfig().getMaxSubmitCount())
                        .limitTime(room.getGameRunningConfig().getLimitTime())
                        .countUsersInRoom(room.getUserCount())
                        .build())
                .toList();

        ResponseDto responseDto = ResponseDto.builder()
                .message(RoomConfigCode.GET_GAME_ROOM_LIST_SUCCESS.getMessage())
                .status(RoomConfigCode.GET_GAME_ROOM_LIST_SUCCESS.getStatus())
                .data(responseDtoList)
                .build();

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    private LeaveRoomUserStatus alreadyLeaveRoom(User user) {
        Long alreadyEnterRoomId = sessionService.getRoomIdFromUser(user);
        return roomService.leave(alreadyEnterRoomId, user);
    }
}
