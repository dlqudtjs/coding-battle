package com.dlqudtjs.codingbattle.repository.user;

import com.dlqudtjs.codingbattle.common.constant.UserRoleType;
import com.dlqudtjs.codingbattle.entity.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByName(UserRoleType name);
}
