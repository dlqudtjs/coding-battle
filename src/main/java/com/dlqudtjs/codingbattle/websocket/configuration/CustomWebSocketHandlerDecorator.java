package com.dlqudtjs.codingbattle.websocket.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

@Component
@Slf4j
public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {
    public CustomWebSocketHandlerDecorator(@Qualifier("customWebSocketHandlerDecorator") WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        WebsocketSessionHolder.addSession(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        super.afterConnectionClosed(session, closeStatus);
        WebsocketSessionHolder.removeSession(session.getId());
    }


}
