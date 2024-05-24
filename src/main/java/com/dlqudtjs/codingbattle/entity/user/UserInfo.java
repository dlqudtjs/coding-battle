package com.dlqudtjs.codingbattle.entity.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfo {

    private User user;
    private UserSetting userSetting;
}
