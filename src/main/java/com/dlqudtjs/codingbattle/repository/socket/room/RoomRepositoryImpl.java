package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.model.room.WaitRoom;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final ConcurrentHashMap<Integer, WaitRoom> roomMap = new ConcurrentHashMap<>();

    @Override
    public Integer save(WaitRoom waitRoom) {
        Integer roomId = availableRoomId();
        roomMap.put(roomId, waitRoom);
        return roomId;
    }

    @Override
    public void joinRoom(String userId, Integer roomId) {
        roomMap.get(roomId).addUser(userId);
    }

    @Override
    public void leaveRoom(String userId, Integer roomId) {
        // todo : 방장이 나가면 방 삭제
        WaitRoom room = roomMap.get(roomId);
        room.removeUser(userId);
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
