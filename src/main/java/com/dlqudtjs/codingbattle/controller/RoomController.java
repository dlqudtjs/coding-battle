package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.constant.code.RoomConfigCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomListResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomInfoResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomEnterUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomLeaveUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.LeaveUserStatus;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final SessionService sessionService;
    private final UserService userService;
    private final SocketRoomController socketRoomController;

    @PostMapping("/v1/room")
    public ResponseEntity<ResponseDto> createRoom(@Valid @RequestBody RoomCreateRequestDto requestDto,
                                                  @RequestHeader("Authorization") String token) {
        requestDto.validate();
        UserInfo userInfo = userService.getUserInfo(jwtTokenProvider.getUserName(token));

        LeaveUserStatus leaveUserStatus = alreadyLeaveRoom(userInfo.getUser());
        sendLeaveRoomUserStatusMessage(leaveUserStatus.getRoomId(), leaveUserStatus);

        Room room = roomService.create(requestDto, userInfo.getUser());

        if (room == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.builder()
                    .message(RoomConfigCode.CREATE_GAME_ROOM_FAIL.getMessage())
                    .status(RoomConfigCode.CREATE_GAME_ROOM_FAIL.getStatus().value())
                    .data(null)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                .message(RoomConfigCode.CREATE_GAME_ROOM_SUCCESS.getMessage())
                .status(RoomConfigCode.CREATE_GAME_ROOM_SUCCESS.getStatus().value())
                .data(getRoomInfoResponseDto(room))
                .build());
    }

    @PostMapping("/v1/room/enter")
    public ResponseEntity<ResponseDto> enterRoom(@Valid @RequestBody RoomEnterRequestDto requestDto,
                                                 @RequestHeader("Authorization") String token) {
        UserInfo userInfo = userService.getUserInfo(jwtTokenProvider.getUserName(token));

        LeaveUserStatus leaveUserStatus = alreadyLeaveRoom(userInfo.getUser());
        sendLeaveRoomUserStatusMessage(leaveUserStatus.getRoomId(), leaveUserStatus);

        Room enterdRoom = roomService.enter(requestDto);

        if (enterdRoom == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.builder()
                    .message(RoomConfigCode.PASSWORD_NOT_MATCH.getMessage())
                    .status(RoomConfigCode.PASSWORD_NOT_MATCH.getStatus().value())
                    .data(null)
                    .build());
        }

        sendEnterRoomUserStatusMessage(enterdRoom.getRoomId(), userInfo);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                .message(RoomConfigCode.JOIN_GAME_ROOM_SUCCESS.getMessage())
                .status(RoomConfigCode.JOIN_GAME_ROOM_SUCCESS.getStatus().value())
                .data(getRoomInfoResponseDto(enterdRoom))
                .build());
    }

    @PostMapping("/v1/room/leave/{roomId}")
    public ResponseEntity<ResponseDto> leaveRoom(@PathVariable("roomId") Long roomId,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        LeaveUserStatus leaveUserStatus = roomService.leave(roomId, user);

        socketRoomController.sendToRoom(
                roomId,
                RoomLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(RoomLeaveUserStatusResponseDto.builder()
                                .roomId(leaveUserStatus.getRoomId())
                                .userId(leaveUserStatus.getUser().getUserId())
                                .isHost(leaveUserStatus.getIsHost())
                                .build())
                        .build()
        );

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/v1/roomList")
    public ResponseEntity<ResponseDto> getRoomList() {
        List<Room> roomList = roomService.getRoomList();

        List<RoomListResponseDto> responseDtoList = roomList.stream()
                .map(room -> RoomListResponseDto.builder()
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
                .status(RoomConfigCode.GET_GAME_ROOM_LIST_SUCCESS.getStatus().value())
                .data(responseDtoList)
                .build();

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    private void sendEnterRoomUserStatusMessage(Long roomId, UserInfo userInfo) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                RoomEnterUserStatusMessageResponseDto.builder()
                        .enterUserStatus(RoomUserStatusResponseDto.builder()
                                .userId(userInfo.getUser().getUserId())
                                .isReady(false)
                                .language(userInfo.getUserSetting().getLanguage().getName())
                                .build())
                        .build()
        );
    }

    private void sendLeaveRoomUserStatusMessage(Long roomId, LeaveUserStatus leaveUserStatus) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                RoomLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(RoomLeaveUserStatusResponseDto.builder()
                                .roomId(leaveUserStatus.getRoomId())
                                .userId(leaveUserStatus.getUser().getUserId())
                                .isHost(leaveUserStatus.getIsHost())
                                .build())
                        .build()
        );
    }

    private RoomInfoResponseDto getRoomInfoResponseDto(Room room) {
        List<RoomUserStatusResponseDto> userStatus = room.getRoomUserStatusList().stream()
                .map(roomUserStatus -> RoomUserStatusResponseDto.builder()
                        .userId(roomUserStatus.getUserId())
                        .isReady(roomUserStatus.getIsReady())
                        .language(roomUserStatus.getUseLanguage().getLanguageName())
                        .build())
                .toList();

        return RoomInfoResponseDto.builder()
                .roomStatus(room.toRoomStatusResponseDto())
                .userStatus(userStatus)
                .build();
    }

    private LeaveUserStatus alreadyLeaveRoom(User user) {
        Long alreadyEnterRoomId = sessionService.getRoomIdFromUser(user);
        return roomService.leave(alreadyEnterRoomId, user);
    }
}
