package com.dlqudtjs.codingbattle.controller;

import static com.dlqudtjs.codingbattle.common.exception.socket.SocketErrorCode.JSON_PARSE_ERROR;

import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.socket.CustomSocketException;
import com.dlqudtjs.codingbattle.dto.room.requestdto.messagewrapperdto.RoomStatusUpdateMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomUserStatusUpdateRequestDto;
import com.dlqudtjs.codingbattle.dto.room.requestdto.SendToRoomMessageRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.SendToRoomMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomUserStatusUpdateMessageResponseDto;
import com.dlqudtjs.codingbattle.service.room.RoomService;
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
    public void sendToRoom(@DestinationVariable("roomId") Long roomId, String message,
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
        } catch (Custom4XXException e) {
            throw new Custom4XXException(e.getMessage(), e.getStatus());
        }
    }

    public void sendToRoom(Long roomId, Object responseDto) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, responseDto);
    }

    @MessageMapping("/room/{roomId}/update/room-status")
    public void updateRoom(@DestinationVariable("roomId") Long roomId, String message,
                           SimpMessageHeaderAccessor headerAccessor) {
        // json -> dto
        RoomStatusUpdateMessageRequestDto roomStatusUpdateMessageRequestDto =
                (RoomStatusUpdateMessageRequestDto)
                        parseMessage(message, new RoomStatusUpdateMessageRequestDto());

        roomStatusUpdateMessageRequestDto.validate();
        try {
            RoomStatusUpdateMessageResponseDto responseDto = roomService.updateRoomStatus(
                    roomId, headerAccessor.getSessionId(), roomStatusUpdateMessageRequestDto);

            // 방에 메시지 전송
            messagingTemplate.convertAndSend("/topic/room/" + roomId, responseDto);
        } catch (Custom4XXException e) {
            throw new Custom4XXException(e.getMessage(), e.getStatus());
        }
    }

    @MessageMapping("/room/{roomId}/update/user-status")
    public void updateUserStatus(@DestinationVariable("roomId") Long roomId, String message,
                                 SimpMessageHeaderAccessor headerAccessor) {
        // json -> dto
        RoomUserStatusUpdateRequestDto roomUserStatusUpdateRequestDto =
                (RoomUserStatusUpdateRequestDto) parseMessage(message, new RoomUserStatusUpdateRequestDto());

        try {
            roomUserStatusUpdateRequestDto.validate();

            RoomUserStatusUpdateMessageResponseDto responseDto = roomService.updateRoomUserStatus(
                    roomId, headerAccessor.getSessionId(), roomUserStatusUpdateRequestDto);

            // 방에 메시지 전송
            messagingTemplate.convertAndSend("/topic/room/" + roomId, responseDto);
        } catch (Custom4XXException e) {
            throw new Custom4XXException(e.getMessage(), e.getStatus());
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
            throw new CustomSocketException(JSON_PARSE_ERROR.getMessage());
        }
    }
}
