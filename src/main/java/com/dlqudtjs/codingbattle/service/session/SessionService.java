package com.dlqudtjs.codingbattle.service.session;

public interface SessionService {

    void enterRoom(String userId, Integer roomId);

    void leaveRoom(String userId);

    Integer getUserInRoomId(String userId);
}
