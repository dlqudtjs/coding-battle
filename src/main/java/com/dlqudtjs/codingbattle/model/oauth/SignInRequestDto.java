package com.dlqudtjs.codingbattle.model.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequestDto {
    private String userId;
    private String password;
}
