package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.oauth.SignInRequestDto;
import com.dlqudtjs.codingbattle.model.oauth.SignUpRequestDto;
import com.dlqudtjs.codingbattle.service.oauth.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/v1/oauth/sign-up")
    public ResponseEntity<ResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        ResponseDto responseDto = oAuthService.singUp(signUpRequestDto);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping("/v1/oauth/sign-in")
    public ResponseEntity<ResponseDto> signIn(@RequestBody SignInRequestDto signInRequestDto) {
        ResponseDto responseDto = oAuthService.signIn(signInRequestDto);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }
}
