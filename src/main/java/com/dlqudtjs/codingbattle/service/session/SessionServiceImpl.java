package com.dlqudtjs.codingbattle.service.session;

import com.dlqudtjs.codingbattle.repository.socket.sessiontatus.SessionStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionStatusRepository sessionStatusRepository;

    @Override
    public void enterRoom(String userId, Integer roomId) {
        sessionStatusRepository.enterRoom(userId, roomId);
    }

    @Override
    public void exitRoom(String userId) {
        sessionStatusRepository.exitRoom(userId);
    }
}
