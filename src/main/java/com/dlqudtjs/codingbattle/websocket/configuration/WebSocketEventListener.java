package com.dlqudtjs.codingbattle.websocket.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@Log4j2
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
    }
}
