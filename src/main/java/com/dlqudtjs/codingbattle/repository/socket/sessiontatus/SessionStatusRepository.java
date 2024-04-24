package com.dlqudtjs.codingbattle.repository.socket.sessiontatus;

public interface SessionStatusRepository {

    void initSessionStatus(String userId);

    void removeSessionStatus(String userId);

    void enterRoom(String userId, Integer roomId);

    void leaveRoom(String userId);

    Integer getUserInRoomId(String userId);
}
