package com.dlqudtjs.codingbattle.service.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    LOGIN_SUCCESS(200, "로그인 성공");

    private final int status;
    private final String message;
}
