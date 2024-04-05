package com.dlqudtjs.codingbattle.common.exception;

import com.dlqudtjs.codingbattle.common.dto.ErrorResponseDto;
import com.dlqudtjs.codingbattle.service.oauth.exception.AlreadyExistNicknameException;
import com.dlqudtjs.codingbattle.service.oauth.exception.AlreadyExistUserIdException;
import com.dlqudtjs.codingbattle.service.oauth.exception.CustomExpiredJwtException;
import com.dlqudtjs.codingbattle.service.oauth.exception.CustomMalformedJwtException;
import com.dlqudtjs.codingbattle.service.oauth.exception.CustomSignatureException;
import com.dlqudtjs.codingbattle.service.oauth.exception.CustomUnsupportedJwtException;
import com.dlqudtjs.codingbattle.service.oauth.exception.ErrorCode;
import com.dlqudtjs.codingbattle.service.oauth.exception.PasswordCheckException;
import com.dlqudtjs.codingbattle.service.oauth.exception.PasswordNotMatchException;
import com.dlqudtjs.codingbattle.service.oauth.exception.RefreshTokenNotFoundException;
import com.dlqudtjs.codingbattle.service.oauth.exception.UnknownException;
import com.dlqudtjs.codingbattle.service.oauth.exception.UserIdNotFoundException;
import io.jsonwebtoken.security.SignatureException;
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

    // 401 SignatureException
    @ExceptionHandler(CustomSignatureException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleSignatureException(SignatureException e) {
        log.error("handleSignatureException", e);
        return buildErrorResponse(ErrorCode.SIGNATURE.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 401 MalformedJwtException
    @ExceptionHandler(CustomMalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleMalformedJwtException() {
        return buildErrorResponse(ErrorCode.MALFORMED_JWT.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 401 ExpiredJwtException
    @ExceptionHandler(CustomExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleExpiredJwtException() {
        return buildErrorResponse(ErrorCode.EXPIRED_JWT.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 401 UnsupportedJwtException
    @ExceptionHandler(CustomUnsupportedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleUnsupportedOperationException() {
        return buildErrorResponse(ErrorCode.UNSUPPORTED_JWT.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 401 JwtUnknownException
    @ExceptionHandler(UnknownException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleJwtUnknownException() {
        return buildErrorResponse(ErrorCode.UNKNOWN.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 401 RefreshTokenNotFoundException
    @ExceptionHandler(RefreshTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        return buildErrorResponse(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
