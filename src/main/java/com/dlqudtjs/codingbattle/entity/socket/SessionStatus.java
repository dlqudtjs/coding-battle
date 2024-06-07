package com.dlqudtjs.codingbattle.entity.socket;

import com.dlqudtjs.codingbattle.common.constant.RoomConfig;
import lombok.Getter;

/*
 * 세션 상태를 나타내는 클래스
 */

public class SessionStatus {

    @Getter
    private Long enterRoomId;
    // todo : 추후 세션 상태 추가 (ex. 방해 금지)
    private Boolean isGameInProgress;


    public SessionStatus() {
        this.enterRoomId = RoomConfig.NO_ROOM_ID.getValue();
        this.isGameInProgress = false;
    }

    public void enterRoom(Long roomId) {
        this.enterRoomId = roomId;
    }

    public void leaveRoom() {
        this.enterRoomId = RoomConfig.NO_ROOM_ID.getValue();

        if (this.isGameInProgress) {
            this.isGameInProgress = false;
        }
    }

    public void startGame() {
        this.isGameInProgress = true;
    }

    public void endGame() {
        this.isGameInProgress = false;
    }

    public Boolean isGameInProgress() {
        return this.isGameInProgress;
    }
}
