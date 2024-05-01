package com.dlqudtjs.codingbattle.websocket.configuration;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.Header;
import com.dlqudtjs.codingbattle.repository.socket.sessiontatus.SessionStatusRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.RoomService;
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
    private final RoomService roomService;

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

            // 유저 세션 상태 추가
            sessionStatusRepository.initSessionStatus(userId);
            // userId와 sessionId를 매핑
            WebsocketSessionHolder.addSession(userId, headerAccessor.getSessionId());
            // 입장한 유저는 0번방을 입장 및 방에 입장한 상태로 변경
            roomService.enterGameRoom(GameSetting.DEFAULT_ROOM_ID.getValue(), userId);
        }

        if (headerAccessor.getCommand() == StompCommand.DISCONNECT) {
            String userId = WebsocketSessionHolder.getUserIdFromSessionId(headerAccessor.getSessionId());

            if (userId == null) {
                return message;
            }

            // 유저 세션 삭제 & 방에서 나가기
            roomService.logout(userId);
            // 유저의 세션 상태 삭제
            sessionStatusRepository.removeSessionStatus(userId);
            // userId와 sessionId 매핑 삭제
            WebsocketSessionHolder.removeSessionFromUserId(userId);
        }

        return message;
    }
}
