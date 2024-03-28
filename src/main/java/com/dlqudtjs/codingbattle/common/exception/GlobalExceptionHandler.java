package com.dlqudtjs.codingbattle.common.exception;

import com.dlqudtjs.codingbattle.common.dto.ErrorResponseDto;
import com.dlqudtjs.codingbattle.service.oauth.exception.AlreadyExistUserIdException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildErrorResponse(Exception exception, String message, HttpStatus status) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .status(status.value())
                .exception(exception.getClass().getName())
                .message(message)
                .build();

        return ResponseEntity.status(status).body(errorResponseDto);
    }
}
