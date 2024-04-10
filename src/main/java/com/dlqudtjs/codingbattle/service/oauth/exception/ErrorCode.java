package com.dlqudtjs.codingbattle.service.oauth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    ALREADY_EXIST_USER_ID(409, "이미 존재하는 아이디입니다."),
    ALREADY_EXIST_NICKNAME(409, "이미 존재하는 닉네임입니다."),
    PASSWORD_CHECK(400, "비밀번호가 일치하지 않습니다."),
    USER_ID_NOT_FOUNT(400, "존재하지 않는 유저입니다."),
    PASSWORD_NOT_MATCH(400, "비밀번호가 일치하지 않습니다."),
    SIGNATURE(401, "JWT 서명이 올바르지 않습니다."),
    EXPIRED_JWT(401, "JWT 토큰이 만료되었습니다."),
    MALFORMED_JWT(401, "JWT 토큰이 올바르지 않습니다."),
    UNSUPPORTED_JWT(401, "지원하지 않는 JWT 토큰입니다."),
    TOKEN_NOT_FOUND(400, "토큰이 존재하지 않습니다."),
    UNKNOWN(500, "알 수 없는 오류가 발생했습니다."),
    REFRESH_TOKEN_NOT_FOUND(400, "리프레시 토큰이 존재하지 않습니다."),
    ;

    private final int status;
    private final String message;
}
