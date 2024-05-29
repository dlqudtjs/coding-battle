package com.dlqudtjs.codingbattle.repository.socket.sessiontatus;

import com.dlqudtjs.codingbattle.entity.user.User;

public interface SessionStatusRepository {

    void initSessionStatus(User user);

    void removeSessionStatus(User user);

    void enterRoom(User user, Long roomId);

    void leaveRoom(User user);

    Long getRoomIdFromUser(User user);

    Boolean isUserInGame(User user);

    void startGame(User user);
}
