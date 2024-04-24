package com.dlqudtjs.codingbattle.repository.socket.sessiontatus;

import com.dlqudtjs.codingbattle.model.socket.SessionStatus;
import com.dlqudtjs.codingbattle.service.room.exception.ErrorCode;
import com.dlqudtjs.codingbattle.websocket.configuration.exception.CustomSocketException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionStatusRepositoryImpl implements SessionStatusRepository {

    private final ConcurrentHashMap<String, SessionStatus> sessionStatusMap = new ConcurrentHashMap<>();

    @Override
    public void initSessionStatus(String userId) {
        sessionStatusMap.put(userId, new SessionStatus());
    }

    @Override
    public void removeSessionStatus(String userId) {
        sessionStatusMap.remove(userId);
    }

    @Override
    public void enterRoom(String userId, Integer roomId) {
        if (!sessionStatusMap.containsKey(userId)) {
            throw new CustomSocketException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }

        sessionStatusMap.get(userId).enterRoom(roomId);
    }

    @Override
    public void leaveRoom(String userId) {
        if (!sessionStatusMap.containsKey(userId)) {
            throw new CustomSocketException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }

        sessionStatusMap.get(userId).leaveRoom();
    }

    @Override
    public Integer getUserInRoomId(String userId) {
        if (!sessionStatusMap.containsKey(userId)) {
            throw new CustomSocketException(ErrorCode.NOT_CONNECT_USER.getMessage());
        }

        return sessionStatusMap.get(userId).getEnterRoomId();
    }
}
