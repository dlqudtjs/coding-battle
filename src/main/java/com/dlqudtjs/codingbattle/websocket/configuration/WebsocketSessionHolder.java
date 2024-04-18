package com.dlqudtjs.codingbattle.websocket.configuration;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
public class WebsocketSessionHolder {
    private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> socketMap = new ConcurrentHashMap<>();

    public static void addSession(String userId, String sessionId) {
        if (socketMap.containsKey(userId)) {
            removeSessionFromSessionId(socketMap.get(userId));
            socketMap.remove(userId);
        }

        socketMap.put(userId, sessionId);
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

    public static void removeSessionFromUserId(String userId) {
        socketMap.remove(userId);
    }

    public static WebSocketSession getSessionFromUserId(String userId) {
        return sessions.get(socketMap.get(userId));
    }

    public static String getUserIdFromSessionId(String sessionId) {
        return socketMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(sessionId))
                .map(ConcurrentHashMap.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public static boolean existUser(String userId) {
        return socketMap.containsKey(userId);
    }
}
