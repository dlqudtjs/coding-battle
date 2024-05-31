package com.dlqudtjs.codingbattle.websocket.configuration;

import com.dlqudtjs.codingbattle.common.constant.Header;
import com.dlqudtjs.codingbattle.common.constant.RoomConfig;
import com.dlqudtjs.codingbattle.dto.room.requestdto.RoomEnterRequestDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomLeaveUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.LeaveRoomUserStatus;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketEventListener {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionService sessionService;
    private final RoomService roomService;
    private final UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String token = headerAccessor.getFirstNativeHeader(Header.AUTHORIZATION.getHeaderName());

        System.out.println(1);
        // token이 유효한지 확인
        jwtTokenProvider.validateToken(token);
        System.out.println(2);

        User user = userService.getUser(jwtTokenProvider.getUserName(token));

        // 유저 세션 상태 추가
        sessionService.initSessionStatus(user);

        // userId와 sessionId를 매핑
        WebsocketSessionHolder.addSession(user, headerAccessor.getSessionId());

        // 소켓을 연결한 유저는 Default 방을 입장
        roomService.enter(RoomEnterRequestDto.builder()
                .roomId(RoomConfig.DEFAULT_ROOM_ID.getValue())
                .userId(user.getUserId())
                .password("")
                .build());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        User userFromSessionId = WebsocketSessionHolder.getUserFromSessionId(event.getSessionId());
        Long roomIdFromUser = sessionService.getRoomIdFromUser(userFromSessionId);

        LeaveRoomUserStatus leaveRoomUserStatus = roomService.leave(roomIdFromUser, userFromSessionId);
        sessionService.removeSessionStatus(userFromSessionId);
        WebsocketSessionHolder.removeSessionFromUserId(userFromSessionId);

        simpMessagingTemplate.convertAndSend("/topic/room/" + roomIdFromUser,
                RoomLeaveUserStatusMessageResponseDto.builder()
                        .leaveUserStatus(RoomLeaveUserStatusResponseDto.builder()
                                .roomId(leaveRoomUserStatus.getRoomId())
                                .userId(leaveRoomUserStatus.getUser().getUserId())
                                .isHost(leaveRoomUserStatus.getIsHost())
                                .build())
                        .build()
        );
    }
}
