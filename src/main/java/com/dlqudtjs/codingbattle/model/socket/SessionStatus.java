package com.dlqudtjs.codingbattle.model.socket;

import com.dlqudtjs.codingbattle.common.constant.GameSetting;
import lombok.Getter;

/*
 * 세션 상태를 나타내는 클래스
 */
@Getter
public class SessionStatus {

    private Integer enterRoomId;
    // todo : 추후 세션 상태 추가 (ex. 방해 금지)

    public SessionStatus() {
        this.enterRoomId = GameSetting.DEFAULT_ROOM_ID.getValue();
    }

    public void enterRoom(Integer roomId) {
        this.enterRoomId = roomId;
    }

    public void leaveRoom() {
        this.enterRoomId = null;
    }
}
