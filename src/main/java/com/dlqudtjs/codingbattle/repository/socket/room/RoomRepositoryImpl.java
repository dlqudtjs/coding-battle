package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.entity.room.Room;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final ConcurrentHashMap<Long, Room> roomMap = new ConcurrentHashMap<>(
            Map.of((long) GameSetting.DEFAULT_ROOM_ID.getValue(),
                    new Room(
                            null,
                            (long) GameSetting.DEFAULT_ROOM_ID.getValue(),
                            null,
                            "default",
                            "default",
                            GameSetting.DEFAULT_ROOM_MAX_USER_COUNT.getValue()
                    )
            )
    );

    @Override
    public Room save(Room room, Long roomId) {
        roomMap.put(roomId, room);
        return roomMap.get(roomId);
    }

    @Override
    public Room join(UserInfo userInfo, Long roomId) {
        roomMap.get(roomId).enter(userInfo);
        return roomMap.get(roomId);
    }

    @Override
    public void leave(Long roomId, User user) {
        if (roomMap.get(roomId).isHost(user)) {
            roomMap.remove(roomId);
            return;
        }

        roomMap.get(roomId).leave(user);
    }

    @Override
    public Room getGameRoom(Long roomId) {
        return roomMap.get(roomId);
    }

    @Override
    public List<Room> getGameRoomList() {
        return List.copyOf(roomMap.values());
    }

    @Override
    public Room updateGameRoomStatus(Long roomId, Room room) {
        roomMap.put(roomId, room);
        return roomMap.get(roomId);
    }

    @Override
    public Boolean isExistRoom(Long roomId) {
        return roomMap.containsKey(roomId);
    }

    @Override
    public Boolean isStartedGame(Long roomId) {
        return roomMap.get(roomId).isStarted();
    }

    @Override
    public Boolean isFullRoom(Long roomId) {
        return roomMap.get(roomId).isFull();
    }

    @Override
    public Boolean isExistUserInRoom(User user, Long roomId) {
        return roomMap.get(roomId).isExistUser(user);
    }

    @Override
    public Long getNewRoomId() {
        Long roomId = 1L;

        while (roomMap.containsKey(roomId)) {
            roomId++;
        }

        return roomId;
    }
}
