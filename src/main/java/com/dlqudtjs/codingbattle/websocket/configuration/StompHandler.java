package com.dlqudtjs.codingbattle.websocket.configuration;

import com.dlqudtjs.codingbattle.common.constant.Header;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        // 기본적인 socket session info logging
        log.info("headerAccessor: {}", headerAccessor);

        if (headerAccessor.getCommand() == StompCommand.CONNECT) {
            String token = headerAccessor.getFirstNativeHeader(Header.AUTHORIZATION.getHeaderName());

            jwtTokenProvider.validateToken(token);
            String userId = jwtTokenProvider.getUserName(token);
            // todo 세션이 전체 구독하고 있는 토픽을 구독하는 로직 추가

            // todo 중복 접속으로 끊어진 세션에게 알림을 보내기 위한 로직 추가
//            String sessionId = WebsocketSessionHolder.getSessionId(userId);
//            if (sessionId != null) {
//                messagingTemplate.convertAndSend(sessionId, "이미 다른 기기에서 접속되어 있습니다.");
//            }

            try {
                WebsocketSessionHolder.addSession(userId, headerAccessor.getSessionId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (headerAccessor.getCommand() == StompCommand.SEND) {
            // todo 방에 메시지를 보낼 때 해당 방에 존재하는지 확인하는 로직 추가
            log.info("session id: {}", headerAccessor.getSessionId());
            log.info("destination: {}", headerAccessor.getDestination());
            String str = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
            log.info("message: {}", str);
        }

        return message;
    }
}
