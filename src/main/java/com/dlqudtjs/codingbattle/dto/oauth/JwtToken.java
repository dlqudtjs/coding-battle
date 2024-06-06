package com.dlqudtjs.codingbattle.dto.oauth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JwtToken {

    private final String accessToken;
    private final String refreshToken;
}
