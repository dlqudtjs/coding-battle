package com.dlqudtjs.codingbattle.common.constant.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OauthConfigCode {
    SIGN_UP_SUCCESS("회원가입 성공", HttpStatus.OK),
    SIGN_IN_SUCCESS("로그인 성공", HttpStatus.OK),
    REFRESH_TOKEN_SUCCESS("토큰 재발급 성공", HttpStatus.OK),
    CHECK_USER_ID_SUCCESS("아이디 중복 확인 성공", HttpStatus.OK),
    ALREADY_EXIST_USER_ID("이미 존재하는 아이디입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_CHECK("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    LANGUAGE_NOT_FOUND("지원하지 않는 언어입니다.", HttpStatus.BAD_REQUEST),
    USER_ID_NOT_FOUNT("존재하지 않는 유저입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    SIGNATURE("JWT 서명이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_JWT("JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    MALFORMED_JWT("JWT 토큰이 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_JWT("지원하지 않는 JWT 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND("토큰이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    UNKNOWN("알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REFRESH_TOKEN_NOT_FOUND("리프레시 토큰이 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    ;;

    private final String message;
    private final HttpStatus status;
}
