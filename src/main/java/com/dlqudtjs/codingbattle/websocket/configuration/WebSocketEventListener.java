package com.dlqudtjs.codingbattle.websocket.configuration;

import com.dlqudtjs.codingbattle.dto.room.responsedto.RoomLeaveUserStatusResponseDto;
import com.dlqudtjs.codingbattle.dto.room.responsedto.messagewrapperdto.RoomLeaveUserStatusMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.room.LeaveRoomUserStatus;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import com.dlqudtjs.codingbattle.service.session.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketEventListener {

    private final SessionService sessionService;
    private final RoomService roomService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
//        User userFromSessionId = WebsocketSessionHolder.getUserFromSessionId(event.getSessionId());
//        sessionService.enterRoom(userFromSessionId, 1L);
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
