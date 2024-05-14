package com.dlqudtjs.codingbattle.common.exception.testcase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TestCaseErrorCode {

    TEST_CASE_FILE_FORMAT_ERROR("파일 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String message;
    private final HttpStatus status;
}
