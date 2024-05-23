package com.dlqudtjs.codingbattle.repository.socket.sessiontatus;

public interface SessionStatusRepository {

    void initSessionStatus(String userId);

    void removeSessionStatus(String userId);

    void enterRoom(String userId, Long roomId);

    void leaveRoom(String userId);

    Long getUserInRoomId(String userId);

    Boolean isUserInGame(String userId);

    void startGame(String userId);
}
