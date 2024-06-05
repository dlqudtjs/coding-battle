package com.dlqudtjs.codingbattle.entity.game;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeaveGameUserStatus {

    String userId;
    Long roomId;
}
