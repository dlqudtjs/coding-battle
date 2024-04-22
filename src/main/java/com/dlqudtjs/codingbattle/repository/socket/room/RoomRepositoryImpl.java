package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.model.room.WaitRoom;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final ConcurrentHashMap<Integer, GameRoom> roomMap = new ConcurrentHashMap<>();

    @Override
    public GameRoom save(GameRoom gameRoom) {
        Integer roomId = availableRoomId();
        gameRoom.setRoomId(roomId);
        roomMap.put(roomId, gameRoom);
        return roomMap.get(roomId);
    }

    @Override
    public GameRoom joinRoom(String userId, Integer roomId) {
        roomMap.get(roomId).addUser(userId);
        return roomMap.get(roomId);
    }

    @Override
    public void leaveRoom(String userId, Integer roomId) {
        if (roomMap.get(roomId).isHost(userId)) {
            roomMap.remove(roomId);
            return;
        }

        roomMap.get(roomId).removeUser(userId);
    }

    @Override
    public Boolean isExistRoom(Integer roomId) {
        return roomMap.containsKey(roomId);
    }

    @Override
    public Boolean isFullRoom(Integer roomId) {
        return roomMap.get(roomId).isFull();
    }

    @Override
    public Boolean isExistUserInRoom(String userId, Integer roomId) {
        return roomMap.get(roomId).isExistUser(userId);
    }

    private Integer availableRoomId() {
        int roomId = 1;

        while (roomMap.containsKey(roomId)) {
            roomId++;
        }

        return roomId;
    }
}
