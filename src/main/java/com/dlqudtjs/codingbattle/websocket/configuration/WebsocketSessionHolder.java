package com.dlqudtjs.codingbattle.websocket.configuration;

import static com.dlqudtjs.codingbattle.common.constant.code.SocketConfigCode.NOT_CONNECT_USER;

import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.entity.user.User;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class WebsocketSessionHolder extends TextWebSocketHandler {

    private static final ConcurrentHashMap<User, String> userAndSessionIdMap = new ConcurrentHashMap<>();

    public static void addUserAndSessionId(User user, String sessionId) {
        userAndSessionIdMap.put(user, sessionId);
    }

    public static void removeSessionIdFromUser(User user) {
        userAndSessionIdMap.remove(user);
    }

    // TODO: getUserFromSessionId -> getSessionIdFromUser 사용하도록 변경하기
    public static String getSessionIdFromUser(User user) {
        return userAndSessionIdMap.get(user);
    }

    public static User getUserFromSessionId(String sessionId) {
        return userAndSessionIdMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(sessionId))
                .map(ConcurrentHashMap.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new Custom4XXException(NOT_CONNECT_USER.getMessage(), NOT_CONNECT_USER.getStatus()));
    }

    public static boolean isNotConnected(User user) {
        return !userAndSessionIdMap.containsKey(user);
    }

    public static boolean isMatched(String sessionId, User user) {
        if (!userAndSessionIdMap.containsKey(user)) {
            return false;
        }

        return userAndSessionIdMap.get(user).equals(sessionId);
    }
}
