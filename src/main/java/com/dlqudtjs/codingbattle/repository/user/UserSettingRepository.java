package com.dlqudtjs.codingbattle.repository.user;

import com.dlqudtjs.codingbattle.entity.user.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
}
