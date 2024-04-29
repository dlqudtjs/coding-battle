package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.model.room.GameRoom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final ConcurrentHashMap<Integer, GameRoom> roomMap = new ConcurrentHashMap<>(
            Map.of(
                    GameSetting.DEFAULT_ROOM_ID.getValue(),
                    GameRoom.builder()
                            .roomId(GameSetting.DEFAULT_ROOM_ID.getValue())
                            .hostId("admin")
                            .title("default")
                            .password("")
                            .language(ProgrammingLanguage.DEFAULT)
                            .isStarted(false)
                            .problemLevel(0)
                            .maxUserCount(GameSetting.DEFAULT_ROOM_MAX_USER_COUNT.getValue())
                            .maxSubmitCount(0)
                            .limitTime(0)
                            .userMap(new ConcurrentHashMap<>())
                            .build()
            )
    );

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
    public GameRoom getGameRoom(Integer roomId) {
        return roomMap.get(roomId);
    }

    @Override
    public List<GameRoom> getGameRoomList() {
        return List.copyOf(roomMap.values());
    }

    @Override
    public GameRoom updateGameRoomStatus(Integer roomId, GameRoom gameRoom) {
        roomMap.put(roomId, gameRoom);
        return roomMap.get(roomId);
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
