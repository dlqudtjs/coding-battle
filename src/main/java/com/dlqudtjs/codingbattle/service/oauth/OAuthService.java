package com.dlqudtjs.codingbattle.service.oauth;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.oauth.SignInRequestDto;
import com.dlqudtjs.codingbattle.model.oauth.SignUpRequestDto;
import jakarta.servlet.http.HttpServletRequest;

public interface OAuthService {

    ResponseDto signIn(SignInRequestDto signInRequestDto);

    ResponseDto singUp(SignUpRequestDto signUpRequestDto);

    ResponseDto refreshToken(HttpServletRequest request);
}
