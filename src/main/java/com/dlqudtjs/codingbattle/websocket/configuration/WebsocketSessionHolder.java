package com.dlqudtjs.codingbattle.websocket.configuration;

import com.dlqudtjs.codingbattle.entity.user.User;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
public class WebsocketSessionHolder {
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<User, String> socketMap = new ConcurrentHashMap<>();

    public static void addSession(User user, String sessionId) {
        if (socketMap.containsKey(user)) {
            removeSessionFromSessionId(socketMap.get(user));
            socketMap.remove(user);
        }

        socketMap.put(user, sessionId);
    }

    public static void addSession(String sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
    }

    public static void removeSessionFromSessionId(String sessionId) {
        try {
            sessions.get(sessionId).close();
        } catch (IOException e) {
            log.error("Failed to close session: {}", e.getMessage());
        }

        sessions.remove(sessionId);
    }

    public static void removeSessionFromUserId(User user) {
        socketMap.remove(user);
    }

    public static WebSocketSession getSessionFromUser(User user) {
        return sessions.get(socketMap.get(user));
    }

    public static User getUserFromSessionId(String sessionId) {
        return socketMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(sessionId))
                .map(ConcurrentHashMap.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static boolean isNotConnected(User user) {
        return !socketMap.containsKey(user);
    }

    public static boolean isMatched(String sessionId, User user) {
        if (!socketMap.containsKey(user)) {
            return false;
        }

        return socketMap.get(user).equals(sessionId);
    }
}
