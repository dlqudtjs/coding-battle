package com.dlqudtjs.codingbattle.websocket.configuration;

import com.dlqudtjs.codingbattle.common.constant.Header;
import com.dlqudtjs.codingbattle.repository.socket.sessiontatus.SessionStatusRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
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
    private final SessionStatusRepository sessionStatusRepository;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        // 기본적인 socket session info logging
        log.info("headerAccessor: {}", headerAccessor);

        if (headerAccessor.getCommand() == StompCommand.CONNECT) {
            String token = headerAccessor.getFirstNativeHeader(Header.AUTHORIZATION.getHeaderName());

            // token이 유효한지 확인
            jwtTokenProvider.validateToken(token);

            String userId = jwtTokenProvider.getUserName(token);

            // userId의 세션 상태 추가
            sessionStatusRepository.addSessionStatus(userId);
            // userId와 sessionId를 매핑
            WebsocketSessionHolder.addSession(userId, headerAccessor.getSessionId());
        }

        if (headerAccessor.getCommand() == StompCommand.SEND) {
            // todo 방에 메시지를 보낼 때 해당 방에 존재하는지 확인하는 로직 추가
            log.info("session id: {}", headerAccessor.getSessionId());
            log.info("destination: {}", headerAccessor.getDestination());
            String str = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
            log.info("message: {}", str);
        }

        if (headerAccessor.getCommand() == StompCommand.DISCONNECT) {
            String userId = WebsocketSessionHolder.getUserIdFromSessionId(headerAccessor.getSessionId());

            // userId의 세션 상태 삭제
            sessionStatusRepository.removeSessionStatus(userId);
            // userId와 sessionId 매핑 삭제
            WebsocketSessionHolder.removeSessionFromUserId(userId);
        }

        return message;
    }
}
