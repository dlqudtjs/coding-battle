package com.dlqudtjs.codingbattle.model.oauth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtTokenDto {

    private final String accessToken;
    private final String refreshToken;
}
