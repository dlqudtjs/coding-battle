package com.dlqudtjs.codingbattle.service.session;

import com.dlqudtjs.codingbattle.entity.user.User;

public interface SessionService {

    void enterRoom(User user, Long roomId);

    void leaveRoom(User user);

    Long getRoomIdFromUser(User user);

    void startGame(User user);

    Boolean isUserInGame(User user);

    void initSessionStatus(User user);

    void removeSessionStatus(User user);
}
