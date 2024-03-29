package com.dlqudtjs.codingbattle.model.oauth;

import com.dlqudtjs.codingbattle.model.user.User;
import com.dlqudtjs.codingbattle.model.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private String userId;
    private String password;
    private String passwordCheck;
    private String nickname;

    public User toEntity() {
        return User.builder()
                .userId(userId)
                .password(password)
                .nickname(nickname)
                .role(UserRole.ROLE_USER)
                .build();
    }
}
