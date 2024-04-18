package com.dlqudtjs.codingbattle.repository.socket.sessiontatus;

import com.dlqudtjs.codingbattle.model.socket.SessionStatus;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionStatusRepositoryImpl implements SessionStatusRepository {

    private final ConcurrentHashMap<String, SessionStatus> sessionStatusMap = new ConcurrentHashMap<>();

    @Override
    public void addSessionStatus(String userId) {
        sessionStatusMap.put(userId, new SessionStatus());
    }

    @Override
    public void removeSessionStatus(String userId) {
        sessionStatusMap.remove(userId);
    }

    @Override
    public void enterRoom(String userId, Integer roomId) {
        sessionStatusMap.get(userId).enterRoom(roomId);
    }

    @Override
    public Boolean isAlreadyInRoom(String userId) {
        if (!sessionStatusMap.containsKey(userId)) {
            return false;
        }

        return sessionStatusMap.get(userId).getEnterRoomId() != null;
    }

    @Override
    public void leaveRoom(String userId) {
        sessionStatusMap.get(userId).exitRoom();
    }
}
