package com.dlqudtjs.codingbattle.dto.oauth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {

    @NotBlank
    private String userId;
    @NotBlank
    private String password;
    @NotBlank
    private String passwordCheck;
    @NotBlank
    private String language;
}
