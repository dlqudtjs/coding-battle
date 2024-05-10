package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import java.util.List;

public interface RoomRepository {

    GameRoom save(GameRoom gameRoom);

    GameRoom joinRoom(String userId, Long roomId);

    void leaveRoom(Long roomId, String userId);

    GameRoom getGameRoom(Long roomId);

    List<GameRoom> getGameRoomList();

    GameRoom updateGameRoomStatus(Long roomId, GameRoom gameRoom);

    Boolean isExistRoom(Long roomId);

    Boolean isFullRoom(Long roomId);

    Boolean isExistUserInRoom(String userId, Long roomId);
}
