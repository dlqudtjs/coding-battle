package com.dlqudtjs.codingbattle.service.oauth;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.oauth.SignInRequestDto;
import com.dlqudtjs.codingbattle.dto.oauth.SignUpRequestDto;
import jakarta.servlet.http.HttpServletRequest;

public interface OAuthService {

    ResponseDto signIn(SignInRequestDto signInRequestDto);

    ResponseDto singUp(SignUpRequestDto signUpRequestDto);

    ResponseDto refreshToken(HttpServletRequest request);

    ResponseDto checkUserId(String userId);
}
