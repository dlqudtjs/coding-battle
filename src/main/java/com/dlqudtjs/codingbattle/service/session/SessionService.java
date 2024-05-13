package com.dlqudtjs.codingbattle.service.session;

public interface SessionService {

    void enterRoom(String userId, Long roomId);

    void leaveRoom(String userId);

    Long getUserInRoomId(String userId);

    void startGame(String userId);
}
