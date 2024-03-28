package com.dlqudtjs.codingbattle.model.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    private String id;
    private String password;
    private String passwordCheck;
    private String nickname;
}
