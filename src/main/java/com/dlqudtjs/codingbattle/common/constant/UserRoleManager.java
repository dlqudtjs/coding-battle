package com.dlqudtjs.codingbattle.common.constant;

import com.dlqudtjs.codingbattle.entity.user.UserRole;
import com.dlqudtjs.codingbattle.repository.user.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserRoleManager {

    private static final Map<String, UserRole> ROLES = new HashMap<>();
    private final UserRoleRepository userRoleRepository;

    public static UserRole ROLE_USER;
    public static UserRole ROLE_ADMIN;

    @PostConstruct
    private void init() {
        userRoleRepository.findAll().forEach(userRole -> {
            ROLES.put(userRole.getName(), userRole);
        });
        setRole();
    }

    public UserRole getUserRole(String roleName) {
        return getRole(roleName);
    }

    private void setRole() {
        ROLE_USER = getRole("ROLE_USER");
        ROLE_ADMIN = getRole("ROLE_ADMIN");
    }

    private UserRole getRole(String roleName) {
        UserRole role = ROLES.get(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Invalid role name: " + roleName);
        }
        return role;
    }
}
