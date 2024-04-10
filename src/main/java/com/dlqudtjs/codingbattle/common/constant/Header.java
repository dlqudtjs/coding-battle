package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Header {
    AUTHORIZATION("Authorization"),
    ;

    private final String headerName;
}
