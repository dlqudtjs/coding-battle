package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.exception.CustomSocketException;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.RoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomUserStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.service.room.RoomService;
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

    @MessageMapping("/room/message/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public SendToRoomMessageResponseDto sendToRoom(
            @DestinationVariable("roomId") Long roomId,
            @Payload SendToRoomMessageRequestDto sendToRoomMessageRequestDto,
            SimpMessageHeaderAccessor headerAccessor) {
        sendToRoomMessageRequestDto.validate();

        return roomService.parseMessage(roomId, headerAccessor.getSessionId(), sendToRoomMessageRequestDto);
    }

    public void sendToRoom(Long roomId, Object responseDto) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, responseDto);
    }

    @MessageMapping("/room/{roomId}/update/room-status")
    @SendTo("/topic/room/{roomId}")
    public RoomStatusUpdateMessageResponseDto updateRoom(
            @DestinationVariable("roomId") Long roomId,
            @Payload RoomStatusUpdateMessageRequestDto roomStatusUpdateMessageRequestDto,
            SimpMessageHeaderAccessor headerAccessor) {
        roomStatusUpdateMessageRequestDto.validate();

        Room room = roomService.updateRoomStatus(
                roomId,
                headerAccessor.getSessionId(),
                roomStatusUpdateMessageRequestDto);

        return RoomStatusUpdateMessageResponseDto.builder()
                .roomStatus(room.toRoomStatusResponseDto())
                .build();
    }

    @MessageMapping("/room/{roomId}/update/user-status")
    @SendTo("/topic/room/{roomId}")
    public RoomUserStatusUpdateMessageResponseDto updateUserStatus(
            @DestinationVariable("roomId") Long roomId,
            @Payload RoomUserStatusUpdateRequestDto roomUserStatusUpdateRequestDto,
            SimpMessageHeaderAccessor headerAccessor) {
        roomUserStatusUpdateRequestDto.validate();

        return roomService.updateRoomUserStatus(roomId, headerAccessor.getSessionId(), roomUserStatusUpdateRequestDto);
    }

    @MessageExceptionHandler
    public void handleException(CustomSocketException e) {
        messagingTemplate.convertAndSend("/topic/error", e.getMessage());
    }
}
