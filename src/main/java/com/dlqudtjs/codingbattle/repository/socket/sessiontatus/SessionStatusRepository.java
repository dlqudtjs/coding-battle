package com.dlqudtjs.codingbattle.repository.socket.sessiontatus;

public interface SessionStatusRepository {

    void addSessionStatus(String userId);

    void removeSessionStatus(String userId);

    void enterRoom(String userId, Integer roomId);

    Boolean isAlreadyInRoom(String userId);

    void exitRoom(String userId);
}
