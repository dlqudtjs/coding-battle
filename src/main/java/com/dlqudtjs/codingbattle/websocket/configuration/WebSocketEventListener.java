package com.dlqudtjs.codingbattle.websocket.configuration;

import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
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

@Component
@RequiredArgsConstructor
@Log4j2
public class WebSocketEventListener {

	private final ScheduledExecutorService scheduledExecutorService;
	private final SimpMessagingTemplate messagingTemplate;
	private final JwtTokenProvider jwtTokenProvider;
	private final SessionService sessionService;
	private final GameController gameController;
	private final RoomController roomController;
	private final RoomService roomService;
	private final UserService userService;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(
			event.getMessage());
		String authorizationHeader = headerAccessor.getFirstNativeHeader(
			Header.AUTHORIZATION.getHeaderName());
		if (authorizationHeader == null) {
			return;
		}

		// token이 유효한지 확인
		jwtTokenProvider.isTokenValid(authorizationHeader);

		User user = userService.getUser(
			jwtTokenProvider.getUserName(authorizationHeader));

		// 이미 연결된 유저인지 확인하여 연결 돼있다면 끊기
		disconnectUser(user);

		// 유저 세션 상태 추가
		sessionService.initSessionStatus(user);

		WebsocketSessionHolder.addUserAndSessionId(user,
			headerAccessor.getSessionId());

		headerAccessor.setUser(user);
		// 소켓을 연결한 유저는 Default 방을 입장
		roomService.enter(RoomEnterRequestDto.builder()
			.roomId(RoomConfig.DEFAULT_ROOM_ID.getValue())
			.userId(user.getUserId())
			.password("")
			.build());
	}

	@EventListener
	public void handleWebSocketDisconnectListener(
		SessionDisconnectEvent event) {
		User user = WebsocketSessionHolder.getUserFromSessionId(
			event.getSessionId());
		Long roomId = sessionService.getRoomIdFromUser(user);
		Room enterRoom = roomService.getRoom(roomId);

		sessionService.removeSessionStatus(user);
		WebsocketSessionHolder.removeSessionIdFromUser(user);

		// 연결이 끊어지고 설정 시간 후에도 다시 연결이 되지 않으면 로그아웃 처리
		scheduledExecutorService.schedule(() -> logout(enterRoom, user),
			GameSetting.SESSION_RETRY_TIME_VALUE,
			java.util.concurrent.TimeUnit.SECONDS
		);
	}

	private void logout(Room room, User user) {
		// 연결이 되어있다면 로그아웃하지 않음
		if (!WebsocketSessionHolder.isNotConnected(user)) {
			log.info("Not logout user : {}", user.getUsername());
			return;
		}

		if (room.isStarted()) {
			gameController.logout(room.getRoomId(), user);
		} else {
			roomController.logout(room.getRoomId(), user);
		}
	}

	private void disconnectUser(User user) {
		if (WebsocketSessionHolder.isNotConnected(user)) {
			return;
		}

		Long roomId = sessionService.getRoomIdFromUser(user);
		Room enterRoom = roomService.getRoom(roomId);

		// client 는 /users/queue/messages 를 구독해야 함
		messagingTemplate.convertAndSendToUser(
			user.getUsername(),
			"/queue/messages",
			"강제 종료");

		if (enterRoom.isStarted()) {
			gameController.logout(roomId, user);
		} else {
			roomController.logout(roomId, user);
		}

		sessionService.removeSessionStatus(user);
		WebsocketSessionHolder.removeSessionIdFromUser(user);
	}
}
