package com.dlqudtjs.codingbattle.service.oauth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_EXIST_USER_ID(409, "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_NICKNAME(409, "이미 존재하는 닉네임입니다."),
    PASSWORD_CHECK(400, "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_USER_ID(400, "존재하지 않는 유저입니다.");

    private final int status;
    private final String message;
}
