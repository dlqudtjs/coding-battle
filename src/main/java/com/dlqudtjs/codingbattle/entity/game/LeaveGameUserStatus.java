package com.dlqudtjs.codingbattle.entity.game;

import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeaveGameUserStatus {

    User user;
    Long roomId;
}
