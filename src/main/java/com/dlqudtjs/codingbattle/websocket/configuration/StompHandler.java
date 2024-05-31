package com.dlqudtjs.codingbattle.websocket.configuration;

import com.dlqudtjs.codingbattle.common.constant.Header;
import com.dlqudtjs.codingbattle.common.constant.RoomConfig;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.repository.socket.SessionStatusRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.user.UserService;
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
    private final UserService userService;
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

            User user = userService.getUser(jwtTokenProvider.getUserName(token));

            // 유저 세션 상태 추가
            sessionStatusRepository.initSessionStatus(user);

            // userId와 sessionId를 매핑
            WebsocketSessionHolder.addSession(user, headerAccessor.getSessionId());

            // 입장한 유저는 0번방을 입장 및 방에 입장한 상태로 변경
            roomService.enter(RoomEnterRequestDto.builder()
                    .roomId(RoomConfig.DEFAULT_ROOM_ID.getValue())
                    .userId(user.getUserId())
                    .password("")
                    .build());
        }

        return message;
    }
}
