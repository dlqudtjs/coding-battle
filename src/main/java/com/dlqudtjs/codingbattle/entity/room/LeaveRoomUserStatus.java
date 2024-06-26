package com.dlqudtjs.codingbattle.entity.room;

import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeaveRoomUserStatus {

    User user;
    Long roomId;
    Boolean isHost;
}
