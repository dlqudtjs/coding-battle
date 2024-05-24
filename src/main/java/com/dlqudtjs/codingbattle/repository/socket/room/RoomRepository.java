package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import java.util.List;

public interface RoomRepository {

    GameRoom save(GameRoom gameRoom);

    GameRoom joinRoom(UserInfo userInfo, Long roomId);

    void leaveRoom(Long roomId, String userId);

    GameRoom getGameRoom(Long roomId);

    List<GameRoom> getGameRoomList();

    GameRoom updateGameRoomStatus(Long roomId, GameRoom gameRoom);

    Boolean isExistRoom(Long roomId);

    Boolean isStartedGame(Long roomId);

    Boolean isFullRoom(Long roomId);

    Boolean isExistUserInRoom(String userId, Long roomId);
}
