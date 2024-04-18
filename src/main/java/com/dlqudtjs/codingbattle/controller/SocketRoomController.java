package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.model.socket.SendToRoomMessageDto;
import com.dlqudtjs.codingbattle.websocket.configuration.exception.CustomSocketException;
import com.dlqudtjs.codingbattle.websocket.configuration.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SocketRoomController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/default/room")
    public void sendToDefaultRoom(String message) {
        SendToRoomMessageDto sendToRoomMessageDto;
        try {
            sendToRoomMessageDto = new ObjectMapper().readValue(message, SendToRoomMessageDto.class);
        } catch (Exception e) {
            throw new CustomSocketException(ErrorCode.JSON_PARSE_ERROR.getMessage());
        }

        messagingTemplate.convertAndSend("/topic/default/room", sendToRoomMessageDto);
    }

    @MessageExceptionHandler
    public void handleException(CustomSocketException e) {
        messagingTemplate.convertAndSend("/topic/error", e.getMessage());
    }
}
