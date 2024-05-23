package com.dlqudtjs.codingbattle.repository.socket.room;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import com.dlqudtjs.codingbattle.entity.user.UserSetting;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final ConcurrentHashMap<Long, GameRoom> roomMap = new ConcurrentHashMap<>(
            Map.of(
                    (long) GameSetting.DEFAULT_ROOM_ID.getValue(),
                    GameRoom.builder()
                            .roomId((long) GameSetting.DEFAULT_ROOM_ID.getValue())
                            .hostId("admin")
                            .title("default")
                            .password("")
                            .language(ProgrammingLanguage.DEFAULT)
                            .isStarted(false)
                            .problemLevel(ProblemLevelType.BRONZE1)
                            .maxUserCount(GameSetting.DEFAULT_ROOM_MAX_USER_COUNT.getValue())
                            .maxSubmitCount(0)
                            .limitTime(0)
                            .userMap(new ConcurrentHashMap<>())
                            .build()
            )
    );

    @Override
    public GameRoom save(GameRoom gameRoom) {
        Long roomId = availableRoomId();
        gameRoom.setRoomId(roomId);
        roomMap.put(roomId, gameRoom);
        return roomMap.get(roomId);
    }

    @Override
    public GameRoom joinRoom(UserSetting userSetting, Long roomId) {
        roomMap.get(roomId).addUser(userSetting);
        return roomMap.get(roomId);
    }

    @Override
    public void leaveRoom(Long roomId, String userId) {
        if (roomMap.get(roomId).isHost(userId)) {
            roomMap.remove(roomId);
            return;
        }

        roomMap.get(roomId).removeUser(userId);
    }

    @Override
    public GameRoom getGameRoom(Long roomId) {
        return roomMap.get(roomId);
    }

    @Override
    public List<GameRoom> getGameRoomList() {
        return List.copyOf(roomMap.values());
    }

    @Override
    public GameRoom updateGameRoomStatus(Long roomId, GameRoom gameRoom) {
        roomMap.put(roomId, gameRoom);
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
    public Boolean isExistUserInRoom(String userId, Long roomId) {
        return roomMap.get(roomId).isExistUser(userId);
    }

    private Long availableRoomId() {
        Long roomId = 1L;

        while (roomMap.containsKey(roomId)) {
            roomId++;
        }

        return roomId;
    }
}
