package com.dlqudtjs.codingbattle.controller;

import static com.dlqudtjs.codingbattle.common.constant.Destination.ROOM_BROADCAST;

import com.dlqudtjs.codingbattle.common.constant.code.RoomConfigCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.GameStartResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.GameStartMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomCreateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomInfoResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomListResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomEnterUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomLeaveUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.LeaveRoomUserStatus;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.game.GameService;
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
    private final SessionService sessionService;
    private final GameService gameService;
    private final RoomService roomService;
    private final UserService userService;

    @PostMapping("/v1/rooms")
    public ResponseEntity<ResponseDto> createRoom(@Valid @RequestBody RoomCreateRequestDto requestDto,
                                                  @RequestHeader("Authorization") String token) {
        UserInfo userInfo = userService.getUserInfo(jwtTokenProvider.getUserName(token));

        LeaveRoomUserStatus leaveRoomUserStatus = alreadyLeaveRoom(userInfo.getUser());
        sendLeaveRoomUserStatusMessage(leaveRoomUserStatus.getRoomId(), leaveRoomUserStatus);

        Room room = roomService.create(requestDto, userInfo.getUser());

        if (room == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.builder()
                    .message(RoomConfigCode.CREATE_ROOM_FAIL.getMessage())
                    .status(RoomConfigCode.CREATE_ROOM_FAIL.getStatus().value())
                    .data(null)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                .message(RoomConfigCode.CREATE_ROOM_SUCCESS.getMessage())
                .status(RoomConfigCode.CREATE_ROOM_SUCCESS.getStatus().value())
                .data(getRoomInfoResponseDto(room))
                .build());
    }

    @PostMapping("/v1/rooms/{roomId}/enter")
    public ResponseEntity<ResponseDto> enterRoom(@Valid @RequestBody RoomEnterRequestDto requestDto,
                                                 @RequestHeader("Authorization") String token) {
        UserInfo userInfo = userService.getUserInfo(jwtTokenProvider.getUserName(token));

        LeaveRoomUserStatus leaveRoomUserStatus = alreadyLeaveRoom(userInfo.getUser());
        sendLeaveRoomUserStatusMessage(leaveRoomUserStatus.getRoomId(), leaveRoomUserStatus);

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
                .message(RoomConfigCode.ENTER_ROOM_SUCCESS.getMessage())
                .status(RoomConfigCode.ENTER_ROOM_SUCCESS.getStatus().value())
                .data(getRoomInfoResponseDto(enterdRoom))
                .build());
    }

    @PostMapping("/v1/rooms/{roomId}/leave")
    public ResponseEntity<ResponseDto> leaveRoom(@PathVariable("roomId") Long roomId,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        LeaveRoomUserStatus leaveRoomUserStatus = roomService.leave(roomId, user);
        sendLeaveRoomUserStatusMessage(roomId, leaveRoomUserStatus);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                .message(RoomConfigCode.LEAVE_ROOM_SUCCESS.getMessage())
                .status(RoomConfigCode.LEAVE_ROOM_SUCCESS.getStatus().value())
                .data(null)
                .build());
    }


    @PostMapping("/v1/rooms/{roomId}/start")
    public ResponseEntity<ResponseDto> startGame(@PathVariable("roomId") Long roomId,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        gameService.startGame(roomId, user);
        sendStartGameMessage(roomId);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.builder()
                .message(RoomConfigCode.START_GAME_SUCCESS.getMessage())
                .status(RoomConfigCode.START_GAME_SUCCESS.getStatus().value())
                .data(null)
                .build());
    }


    @GetMapping("/v1/roomLists")
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
                .message(RoomConfigCode.GET_ROOM_LIST_SUCCESS.getMessage())
                .status(RoomConfigCode.GET_ROOM_LIST_SUCCESS.getStatus().value())
                .data(responseDtoList)
                .build();

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    public void logout(Long roomId, User user) {
        LeaveRoomUserStatus leaveRoomUserStatus = roomService.leave(roomId, user);
        sendLeaveRoomUserStatusMessage(roomId, leaveRoomUserStatus);
    }

    private void sendStartGameMessage(Long roomId) {
        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + roomId,
                GameStartMessageResponseDto.builder()
                        .startMessage(GameStartResponseDto.builder()
                                .message("Game Start")
                                .build())
                        .build());
    }

    private void sendEnterRoomUserStatusMessage(Long roomId, UserInfo userInfo) {
        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + roomId,
                RoomEnterUserStatusMessageResponseDto.builder()
                        .enterUserStatus(RoomUserStatusResponseDto.builder()
                                .userId(userInfo.getUser().getUserId())
                                .isReady(false)
                                .language(userInfo.getUserSetting().getProgrammingLanguage())
                                .build())
                        .build()
        );
    }

    private void sendLeaveRoomUserStatusMessage(Long roomId, LeaveRoomUserStatus leaveRoomUserStatus) {
        messagingTemplate.convertAndSend(ROOM_BROADCAST.getValue() + roomId,
                RoomLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(RoomLeaveUserStatusResponseDto.builder()
                                .roomId(leaveRoomUserStatus.getRoomId())
                                .userId(leaveRoomUserStatus.getUser().getUserId())
                                .isHost(leaveRoomUserStatus.getIsHost())
                                .build())
                        .build()
        );
    }

    private RoomInfoResponseDto getRoomInfoResponseDto(Room room) {
        List<RoomUserStatusResponseDto> userStatus = room.getRoomUserStatusList().stream()
                .map(roomUserStatus -> RoomUserStatusResponseDto.builder()
                        .userId(roomUserStatus.getUserId())
                        .isReady(roomUserStatus.getIsReady())
                        .language(roomUserStatus.getUseLanguage())
                        .build())
                .toList();

        return RoomInfoResponseDto.builder()
                .roomStatus(room.toRoomStatusResponseDto())
                .userStatus(userStatus)
                .build();
    }

    private LeaveRoomUserStatus alreadyLeaveRoom(User user) {
        Long alreadyEnterRoomId = sessionService.getRoomIdFromUser(user);
        return roomService.leave(alreadyEnterRoomId, user);
    }
}
