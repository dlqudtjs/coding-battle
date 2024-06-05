package com.dlqudtjs.codingbattle.websocket.configuration;

import com.dlqudtjs.codingbattle.common.constant.Header;
import com.dlqudtjs.codingbattle.common.constant.RoomConfig;
import com.dlqudtjs.codingbattle.controller.GameController;
import com.dlqudtjs.codingbattle.controller.RoomController;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketEventListener {

    private final JwtTokenProvider jwtTokenProvider;
    private final SessionService sessionService;
    private final GameController gameController;
    private final RoomController roomController;
    private final RoomService roomService;
    private final UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String token = headerAccessor.getFirstNativeHeader(Header.AUTHORIZATION.getHeaderName());

        // token이 유효한지 확인
        jwtTokenProvider.validateToken(token);

        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        // 유저 세션 상태 추가
        sessionService.initSessionStatus(user);

        WebsocketSessionHolder.addUserAndSessionId(user, headerAccessor.getSessionId());

        // 소켓을 연결한 유저는 Default 방을 입장
        roomService.enter(RoomEnterRequestDto.builder()
                .roomId(RoomConfig.DEFAULT_ROOM_ID.getValue())
                .userId(user.getUserId())
                .password("")
                .build());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        User user = WebsocketSessionHolder.getUserFromSessionId(event.getSessionId());
        Long roomId = sessionService.getRoomIdFromUser(user);
        Room enterRoom = roomService.getRoom(roomId);

        if (enterRoom.isStarted()) {
            gameController.logout(roomId, user);
        } else {
            roomController.logout(roomId, user);
        }

        sessionService.removeSessionStatus(user);
        WebsocketSessionHolder.removeSessionIdFromUser(user);
    }
}
