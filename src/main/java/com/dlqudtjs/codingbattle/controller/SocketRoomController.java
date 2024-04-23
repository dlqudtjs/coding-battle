package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.responseDto.GameRoomUserStatusUpdateResponseDto;
import com.dlqudtjs.codingbattle.model.socket.SendToRoomMessageDto;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.websocket.configuration.exception.CustomSocketException;
import com.dlqudtjs.codingbattle.websocket.configuration.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SocketRoomController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MessageMapping("/default/room")
    public void sendToDefaultRoom(String message) {
        SendToRoomMessageDto sendToRoomMessageDto =
                (SendToRoomMessageDto) parseMessage(message, new SendToRoomMessageDto());

        messagingTemplate.convertAndSend("/topic/default/room", sendToRoomMessageDto);
    }

    @MessageMapping("/room/{roomId}/update/room-status")
    public void updateRoom(@DestinationVariable("roomId") Integer roomId, String message,
                           SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();

        GameRoomStatusUpdateRequestDto gameRoomStatusUpdateRequestDto =
                (GameRoomStatusUpdateRequestDto) parseMessage(message, new GameRoomStatusUpdateRequestDto());

        try {
            gameRoomStatusUpdateRequestDto.validate();
        } catch (Custom4XXException e) {
            throw new CustomSocketException(ErrorCode.JSON_PARSE_ERROR.getMessage());
        }

        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                roomService.updateGameRoomStatus(roomId, sessionId, gameRoomStatusUpdateRequestDto));
    }

    @MessageMapping("/room/{roomId}/update/user-status")
    public void updateUserStatus(@DestinationVariable("roomId") String roomId, String message) {
        GameRoomUserStatusUpdateRequestDto gameRoomUserStatusUpdateRequestDto =
                (GameRoomUserStatusUpdateRequestDto) parseMessage(message, new GameRoomUserStatusUpdateRequestDto());

        try {
            gameRoomUserStatusUpdateRequestDto.validate();
        } catch (Custom4XXException e) {
            throw new CustomSocketException(ErrorCode.JSON_PARSE_ERROR.getMessage());
        }

        messagingTemplate.convertAndSend("/topic/room/" + roomId,
                GameRoomUserStatusUpdateResponseDto.builder()
                        .userStatus(gameRoomUserStatusUpdateRequestDto)
                        .build());
    }

    @MessageExceptionHandler
    public void handleException(CustomSocketException e) {
        messagingTemplate.convertAndSend("/topic/error", e.getMessage());
    }

    private Object parseMessage(String message, Object requestDto) {
        try {
            return objectMapper.readValue(message, requestDto.getClass());
        } catch (Exception e) {
            throw new CustomSocketException(ErrorCode.JSON_PARSE_ERROR.getMessage());
        }
    }
}
