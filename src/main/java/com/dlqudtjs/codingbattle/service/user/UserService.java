package com.dlqudtjs.codingbattle.service.user;

import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.entity.user.UserSetting;

public interface UserService {

    UserInfo getUserInfo(String userId);

    User getUser(String userId);

    UserSetting getUserSetting(User user);
}
