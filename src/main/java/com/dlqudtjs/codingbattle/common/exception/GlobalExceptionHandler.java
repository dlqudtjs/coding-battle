package com.dlqudtjs.codingbattle.common.exception;

import com.dlqudtjs.codingbattle.common.dto.ErrorResponseDto;
import io.jsonwebtoken.MalformedJwtException;
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

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        ErrorResponseDto errorResponseDto = ErrorResponseDto.builder()
                .status(status.value())
                .message(message)
                .build();

        return ResponseEntity.status(status).body(errorResponseDto);
    }

    // 500 UnknownException
    @ExceptionHandler(UnknownException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleUnknownException(UnknownException e) {
        log.error("handleUnknownException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 400 BAD_REQUEST
    @ExceptionHandler(CustomSocketException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleCustomSocketException(CustomSocketException e) {
        log.error("CustomSocketException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleMalformedJwtException(MalformedJwtException e) {
        log.error("MalformedJwtException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 4XX custom exception
    @ExceptionHandler(Custom4XXException.class)
    public ResponseEntity<Object> handleCustomEnumException(Custom4XXException e) {
        log.error("CustomEnumException", e);
        return buildErrorResponse(e.getMessage(), e.getStatus());
    }
}
