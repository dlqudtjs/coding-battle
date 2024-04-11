package com.dlqudtjs.codingbattle.websocket.configuration;

import java.nio.charset.StandardCharsets;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class StompExceptionHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(
            Message<byte[]> clientMessage,
            Throwable ex) {

        return errorMessage(ex.getCause().getMessage());
    }

    private Message<byte[]> errorMessage(String errorMessage) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(errorMessage.getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders());
    }
}
