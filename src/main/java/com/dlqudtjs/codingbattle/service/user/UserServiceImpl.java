package com.dlqudtjs.codingbattle.service.user;

import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserSetting;
import com.dlqudtjs.codingbattle.repository.user.UserRepository;
import com.dlqudtjs.codingbattle.repository.user.UserSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSettingRepository userSettingRepository;

    @Override
    @Transactional
    public UserSetting getUserSetting(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(()
                -> new IllegalArgumentException(""));

        return userSettingRepository.findByUserId(user.getId());
    }
}
