package com.dlqudtjs.codingbattle.entity.socket;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;

/*
 * 세션 상태를 나타내는 클래스
 */

public class SessionStatus {

    private Long enterRoomId;
    // todo : 추후 세션 상태 추가 (ex. 방해 금지)
    private Boolean isGameInProgress;


    public SessionStatus() {
        this.enterRoomId = (long) GameSetting.NO_ROOM_ID.getValue();
        this.isGameInProgress = false;
    }

    public void enterRoom(Long roomId) {
        this.enterRoomId = roomId;
    }

    public void leaveRoom() {
        this.enterRoomId = (long) GameSetting.NO_ROOM_ID.getValue();

        if (this.isGameInProgress) {
            this.isGameInProgress = false;
        }
    }

    public void startGame() {
        this.isGameInProgress = true;
    }

    public Boolean isGameInProgress() {
        return this.isGameInProgress;
    }

    public Long getEnterRoomId() {
        return enterRoomId;
    }
}
