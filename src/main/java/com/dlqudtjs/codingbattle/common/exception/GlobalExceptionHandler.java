package com.dlqudtjs.codingbattle.common.exception;

import com.dlqudtjs.codingbattle.common.dto.ErrorResponseDto;
import com.dlqudtjs.codingbattle.common.exception.oauth.AlreadyExistNicknameException;
import com.dlqudtjs.codingbattle.common.exception.oauth.AlreadyExistUserIdException;
import com.dlqudtjs.codingbattle.common.exception.oauth.CustomAuthenticationException;
import com.dlqudtjs.codingbattle.common.exception.oauth.PasswordCheckException;
import com.dlqudtjs.codingbattle.common.exception.oauth.PasswordNotMatchException;
import com.dlqudtjs.codingbattle.common.exception.oauth.UserIdNotFoundException;
import com.dlqudtjs.codingbattle.common.exception.room.CustomRoomException;
import com.dlqudtjs.codingbattle.common.exception.socket.CustomSocketException;
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

    // 409 AlreadyExistUserIdException
    @ExceptionHandler(AlreadyExistUserIdException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleAlreadyExistUserIdException(AlreadyExistUserIdException e) {
        log.error("handleAlreadyExistUserIdException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    // 409 AlreadyExistNicknameException
    @ExceptionHandler(AlreadyExistNicknameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleAlreadyExistNicknameException(AlreadyExistNicknameException e) {
        log.error("handleAlreadyExistNicknameException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    // 400 PasswordCheckException
    @ExceptionHandler(PasswordCheckException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handlePasswordCheckException(PasswordCheckException e) {
        log.error("handlePasswordCheckException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 400 UserIdNotFoundException
    @ExceptionHandler(UserIdNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleUserIdNotFoundException(UserIdNotFoundException e) {
        log.error("handleUserIdNotFoundException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 400 PasswordNotMatchException
    @ExceptionHandler(PasswordNotMatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handlePasswordNotMatchException(PasswordNotMatchException e) {
        log.error("handlePasswordNotMatchException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }


    // 401 CustomAuthenticationException
    @ExceptionHandler(CustomAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleAuthenticationException(CustomAuthenticationException e) {
        log.error("handleAuthenticationException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
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

    // 400 BAD_REQUEST
    @ExceptionHandler(CustomRoomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleCustomRoomException(CustomRoomException e) {
        log.error("CustomRoomException", e);
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 4XX custom exception
    @ExceptionHandler(Custom4XXException.class)
    public ResponseEntity<Object> handleCustomEnumException(Custom4XXException e) {
        log.error("CustomEnumException", e);
        return buildErrorResponse(e.getMessage(), e.getStatus());
    }
}
