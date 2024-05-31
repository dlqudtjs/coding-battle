package com.dlqudtjs.codingbattle.service.session;

import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.repository.socket.SessionStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
 * SessionServiceImpl 클래스는 Session의 상태를 관리하는 클래스임
 * ex. 어디 방에 있는지, 방해 금지 상태인지 등
 */
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionStatusRepository sessionStatusRepository;

    @Override
    public void enterRoom(User user, Long roomId) {
        sessionStatusRepository.enterRoom(user, roomId);
    }

    @Override
    public void leaveRoom(User user) {
        sessionStatusRepository.leaveRoom(user);
    }

    @Override
    public Long getRoomIdFromUser(User user) {
        return sessionStatusRepository.getRoomIdFromUser(user);
    }


    @Override
    public void startGame(User user) {
        sessionStatusRepository.startGame(user);
    }

    @Override
    public Boolean isUserInGame(User user) {
        return sessionStatusRepository.isUserInGame(user);
    }

    @Override
    public void initSessionStatus(User user) {
        sessionStatusRepository.initSessionStatus(user);
    }

    @Override
    public void removeSessionStatus(User user) {
        sessionStatusRepository.removeSessionStatus(user);
    }
}
