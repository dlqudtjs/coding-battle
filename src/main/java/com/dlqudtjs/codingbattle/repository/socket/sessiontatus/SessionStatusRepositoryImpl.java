package com.dlqudtjs.codingbattle.repository.socket.sessiontatus;

import com.dlqudtjs.codingbattle.entity.socket.SessionStatus;
import com.dlqudtjs.codingbattle.common.exception.room.RoomErrorCode;
import com.dlqudtjs.codingbattle.common.exception.socket.CustomSocketException;
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
    public Boolean isUserInGame(String userId) {
        if (!sessionStatusMap.containsKey(userId)) {
            throw new CustomSocketException(RoomErrorCode.NOT_CONNECT_USER.getMessage());
        }

        return sessionStatusMap.get(userId).isGameInProgress();
    }

    @Override
    public void enterRoom(String userId, Long roomId) {
        if (!sessionStatusMap.containsKey(userId)) {
            throw new CustomSocketException(RoomErrorCode.NOT_CONNECT_USER.getMessage());
        }

        sessionStatusMap.get(userId).enterRoom(roomId);
    }

    @Override
    public void leaveRoom(String userId) {
        if (!sessionStatusMap.containsKey(userId)) {
            throw new CustomSocketException(RoomErrorCode.NOT_CONNECT_USER.getMessage());
        }

        sessionStatusMap.get(userId).leaveRoom();
    }

    @Override
    public Long getUserInRoomId(String userId) {
        if (!sessionStatusMap.containsKey(userId)) {
            throw new CustomSocketException(RoomErrorCode.NOT_CONNECT_USER.getMessage());
        }

        return sessionStatusMap.get(userId).getEnterRoomId();
    }

    @Override
    public void startGame(String userId) {
        if (!sessionStatusMap.containsKey(userId)) {
            throw new CustomSocketException(RoomErrorCode.NOT_CONNECT_USER.getMessage());
        }

        sessionStatusMap.get(userId).startGame();
    }
}
