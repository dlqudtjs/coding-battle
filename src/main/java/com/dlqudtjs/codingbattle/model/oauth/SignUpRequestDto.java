package com.dlqudtjs.codingbattle.model.oauth;

import com.dlqudtjs.codingbattle.model.user.User;
import com.dlqudtjs.codingbattle.common.constant.UserRoleType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String password;
    @NotBlank
    private String passwordCheck;
    @NotBlank
    private String language;

    public User toEntity() {
        return User.builder()
                .userId(userId)
                .password(password)
                .role(UserRoleType.ROLE_USER)
                .build();
    }
}
