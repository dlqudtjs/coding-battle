package com.dlqudtjs.codingbattle.service.oauth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_EXIST_USER_ID(409, "이미 존재하는 아이디입니다.");

    private final int status;
    private final String message;
}
