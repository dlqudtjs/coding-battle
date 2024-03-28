package com.dlqudtjs.codingbattle.common.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseDto {

    private final int status;
    private final String message;
    private final Object data;
}
