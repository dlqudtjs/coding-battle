package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.model.room.GameRoom;
import java.util.List;

public interface RoomRepository {

    GameRoom save(GameRoom gameRoom);

    GameRoom joinRoom(String userId, Integer roomId);

    void leaveRoom(Integer roomId, String userId);

    GameRoom getGameRoom(Integer roomId);

    List<GameRoom> getGameRoomList();

    GameRoom updateGameRoomStatus(Integer roomId, GameRoom gameRoom);

    Boolean isExistRoom(Integer roomId);

    Boolean isFullRoom(Integer roomId);

    Boolean isExistUserInRoom(String userId, Integer roomId);
}
