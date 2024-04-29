package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.GameRoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto.GameRoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.model.room.responsedto.messagewrapperdto.GameRoomUserStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.room.exception.CustomRoomException;
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

    @MessageMapping("/room/message/{roomId}")
    public void sendToRoom(@DestinationVariable("roomId") Integer roomId, String message,
                           SimpMessageHeaderAccessor headerAccessor) {
        // json -> dto
        SendToRoomMessageRequestDto sendToRoomMessageRequestDto =
                (SendToRoomMessageRequestDto) parseMessage(message, new SendToRoomMessageRequestDto());

        try {
            // SendToRoomMessageResponseDto로 parse 및 유효성 검사
            SendToRoomMessageResponseDto responseDto = roomService.parseMessage(
                    roomId, headerAccessor.getSessionId(), sendToRoomMessageRequestDto);

            // 방에 메시지 전송
            messagingTemplate.convertAndSend("/topic/room/" + roomId, responseDto);
        } catch (CustomRoomException e) {
            throw new CustomRoomException(e.getMessage());
        }
    }

    public void sendToRoom(Integer roomId, Object responseDto) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, responseDto);
    }

    @MessageMapping("/room/{roomId}/update/room-status")
    public void updateRoom(@DestinationVariable("roomId") Integer roomId, String message,
                           SimpMessageHeaderAccessor headerAccessor) {
        // json -> dto
        GameRoomStatusUpdateRequestDto gameRoomStatusUpdateRequestDto =
                (GameRoomStatusUpdateRequestDto) parseMessage(message, new GameRoomStatusUpdateRequestDto());

        try {
            gameRoomStatusUpdateRequestDto.validate();

            GameRoomStatusUpdateMessageResponseDto responseDto = roomService.updateGameRoomStatus(
                    roomId, headerAccessor.getSessionId(), gameRoomStatusUpdateRequestDto);

            // 방에 메시지 전송
            messagingTemplate.convertAndSend("/topic/room/" + roomId, responseDto);
        } catch (Custom4XXException e) {
            throw new CustomSocketException(ErrorCode.JSON_PARSE_ERROR.getMessage());
        } catch (CustomRoomException e) {
            throw new CustomSocketException(e.getMessage());
        }
    }

    @MessageMapping("/room/{roomId}/update/user-status")
    public void updateUserStatus(@DestinationVariable("roomId") Integer roomId, String message,
                                 SimpMessageHeaderAccessor headerAccessor) {
        // json -> dto
        GameRoomUserStatusUpdateRequestDto gameRoomUserStatusUpdateRequestDto =
                (GameRoomUserStatusUpdateRequestDto) parseMessage(message, new GameRoomUserStatusUpdateRequestDto());

        try {
            gameRoomUserStatusUpdateRequestDto.validate();

            GameRoomUserStatusUpdateMessageResponseDto responseDto = roomService.updateGameRoomUserStatus(
                    roomId, headerAccessor.getSessionId(), gameRoomUserStatusUpdateRequestDto);

            // 방에 메시지 전송
            messagingTemplate.convertAndSend("/topic/room/" + roomId, responseDto);
        } catch (Custom4XXException e) {
            throw new CustomSocketException(ErrorCode.JSON_PARSE_ERROR.getMessage());
        } catch (CustomRoomException e) {
            throw new CustomSocketException(e.getMessage());
        }
    }

    @MessageExceptionHandler
    public void handleException(CustomSocketException e) {
        messagingTemplate.convertAndSend("/topic/error", e.getMessage());
    }

    // parse json -> requestDto
    private Object parseMessage(String message, Object requestDto) {
        try {
            return objectMapper.readValue(message, requestDto.getClass());
        } catch (Exception e) {
            throw new CustomSocketException(ErrorCode.JSON_PARSE_ERROR.getMessage());
        }
    }
}
