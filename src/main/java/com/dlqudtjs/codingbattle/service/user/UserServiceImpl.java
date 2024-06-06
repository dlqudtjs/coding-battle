package com.dlqudtjs.codingbattle.service.user;

import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserInfo;
import com.dlqudtjs.codingbattle.entity.user.UserSetting;
import com.dlqudtjs.codingbattle.repository.user.UserRepository;
import com.dlqudtjs.codingbattle.repository.user.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSettingRepository userSettingRepository;

    @Override
    public User getUser(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(()
                -> new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus()));
    }

    @Override
    public UserInfo getUserInfo(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(()
                -> new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus()));

        UserSetting userSetting = userSettingRepository.findByUserId(user.getId());

        return UserInfo.builder()
                .user(user)
                .userSetting(userSetting)
                .build();
    }
}
