package com.dlqudtjs.codingbattle.service.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    SIGN_UP_SUCCESS(200, "회원가입 성공"),
    SIGN_IN_SUCCESS(200, "로그인 성공");

    private final int status;
    private final String message;
}
