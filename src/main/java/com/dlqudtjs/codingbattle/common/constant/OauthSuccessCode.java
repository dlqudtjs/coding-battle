package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OauthSuccessCode {

    SIGN_UP_SUCCESS(200, "회원가입 성공"),
    SIGN_IN_SUCCESS(200, "로그인 성공"),
    REFRESH_TOKEN_SUCCESS(200, "토큰 재발급 성공"),
    CHECK_USER_ID_SUCCESS(200, "아이디 중복 확인 성공");

    private final int status;
    private final String message;
}
