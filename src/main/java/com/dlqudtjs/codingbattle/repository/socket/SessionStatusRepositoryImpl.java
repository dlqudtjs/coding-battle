package com.dlqudtjs.codingbattle.repository.socket;

import com.dlqudtjs.codingbattle.common.exception.CustomSocketException;
import com.dlqudtjs.codingbattle.common.constant.code.SocketConfigCode;
import com.dlqudtjs.codingbattle.entity.socket.SessionStatus;
import com.dlqudtjs.codingbattle.entity.user.User;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionStatusRepositoryImpl implements SessionStatusRepository {

    private final ConcurrentHashMap<User, SessionStatus> sessionStatusMap = new ConcurrentHashMap<>();

    @Override
    public void initSessionStatus(User user) {
        sessionStatusMap.put(user, new SessionStatus());
    }

    @Override
    public void removeSessionStatus(User user) {
        sessionStatusMap.remove(user);
    }

    @Override
    public Boolean isUserInGame(User user) {
        if (!sessionStatusMap.containsKey(user)) {
            throw new CustomSocketException(SocketConfigCode.NOT_CONNECT_USER.getMessage());
        }

        return sessionStatusMap.get(user).isGameInProgress();
    }

    @Override
    public void enterRoom(User user, Long roomId) {
        if (!sessionStatusMap.containsKey(user)) {
            throw new CustomSocketException(SocketConfigCode.NOT_CONNECT_USER.getMessage());
        }

        sessionStatusMap.get(user).enterRoom(roomId);
    }

    @Override
    public void leaveRoom(User user) {
        if (!sessionStatusMap.containsKey(user)) {
            throw new CustomSocketException(SocketConfigCode.NOT_CONNECT_USER.getMessage());
        }

        sessionStatusMap.get(user).leaveRoom();
    }

    @Override
    public Long getRoomIdFromUser(User user) {
        if (!sessionStatusMap.containsKey(user)) {
            throw new CustomSocketException(SocketConfigCode.NOT_CONNECT_USER.getMessage());
        }

        return sessionStatusMap.get(user).getEnterRoomId();
    }

    @Override
    public void startGame(User user) {
        if (!sessionStatusMap.containsKey(user)) {
            throw new CustomSocketException(SocketConfigCode.NOT_CONNECT_USER.getMessage());
        }

        sessionStatusMap.get(user).startGame();
    }

    @Override
    public void endGame(User user) {
        if (!sessionStatusMap.containsKey(user)) {
            throw new CustomSocketException(SocketConfigCode.NOT_CONNECT_USER.getMessage());
        }

        sessionStatusMap.get(user).endGame();
    }
}
