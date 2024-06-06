package com.dlqudtjs.codingbattle.common.dto;

import com.dlqudtjs.codingbattle.common.util.TimeProvider;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorResponseDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final ZonedDateTime timestamp = TimeProvider.getZonedDateTime();
    private final int status;
    private final String message;
}
