package com.dlqudtjs.codingbattle.controller;

import static com.dlqudtjs.codingbattle.common.constant.Destination.ERROR_BROADCAST_VALUE;
import static com.dlqudtjs.codingbattle.common.constant.Destination.ROOM_BROADCAST_VALUE;

import com.dlqudtjs.codingbattle.common.constant.MessageType;
import com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode;
import com.dlqudtjs.codingbattle.common.exception.CustomSocketException;
import com.dlqudtjs.codingbattle.common.util.Time;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.RoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.SendToRoomMessageWrapperDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomUserStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.room.RoomUserStatus;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SocketRoomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;

    @MessageMapping("/rooms/{roomId}/messages")
    @SendTo(ROOM_BROADCAST_VALUE + "{roomId}")
    public SendToRoomMessageWrapperDto sendToRoom(
            @DestinationVariable("roomId") Long roomId,
            @Payload SendToRoomMessageRequestDto sendToRoomMessageRequestDto,
            SimpMessageHeaderAccessor headerAccessor) {
        User user = WebsocketSessionHolder.getUserFromSessionId(headerAccessor.getSessionId());

        // 방이 존재하지 않거나, 유저가 방에 존재하지 않고, 메시지 전송 유저와 세션 유저가 다를 경우
        if (!roomService.isExistRoom(roomId) || !roomService.isExistUserInRoom(user, roomId) &&
                !user.getUserId().equals(sendToRoomMessageRequestDto.getSenderId())) {
            throw new CustomSocketException(CommonConfigCode.INVALID_INPUT_VALUE.getMessage());
        }

        return SendToRoomMessageWrapperDto.builder()
                .message(SendToRoomMessageResponseDto.builder()
                        .messageType(MessageType.USER)
                        .senderId(user.getUserId())
                        .message(sendToRoomMessageRequestDto.getMessage())
                        .sendTime(Time.getZonedDateTime())
                        .build())
                .build();
    }


    @MessageMapping("/rooms/{roomId}/update/room-status")
    @SendTo(ROOM_BROADCAST_VALUE + "{roomId}")
    public RoomStatusUpdateMessageResponseDto updateRoom(
            @DestinationVariable("roomId") Long roomId,
            @Payload RoomStatusUpdateMessageRequestDto roomStatusUpdateMessageRequestDto,
            SimpMessageHeaderAccessor headerAccessor) {
        User user = WebsocketSessionHolder.getUserFromSessionId(headerAccessor.getSessionId());

        Room room = roomService.updateRoomStatus(
                roomId,
                user,
                roomStatusUpdateMessageRequestDto);

        return RoomStatusUpdateMessageResponseDto.builder()
                .roomStatus(room.toRoomStatusResponseDto())
                .build();
    }

    @MessageMapping("/rooms/{roomId}/update/user-status")
    @SendTo(ROOM_BROADCAST_VALUE + "{roomId}")
    public RoomUserStatusUpdateMessageResponseDto updateUserStatus(
            @DestinationVariable("roomId") Long roomId,
            @Payload RoomUserStatusUpdateRequestDto roomUserStatusUpdateRequestDto,
            SimpMessageHeaderAccessor headerAccessor) {
        RoomUserStatus roomUserStatus = roomService.updateRoomUserStatus(
                roomId,
                headerAccessor.getSessionId(),
                roomUserStatusUpdateRequestDto);

        return RoomUserStatusUpdateMessageResponseDto.builder()
                .updateUserStatus(RoomUserStatusResponseDto.builder()
                        .userId(roomUserStatus.getUserId())
                        .isReady(roomUserStatus.getIsReady())
                        .language(roomUserStatus.getUseLanguage())
                        .build())
                .build();
    }

    @MessageExceptionHandler
    public void handleException(CustomSocketException e) {
        messagingTemplate.convertAndSend(ERROR_BROADCAST_VALUE, e.getMessage());
    }
}
