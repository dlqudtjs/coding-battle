package com.dlqudtjs.codingbattle.common.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProblemLevelType {
    BRONZE5,
    BRONZE4,
    BRONZE3,
    BRONZE2,
    BRONZE1,
    SILVER5,
    SILVER4,
    SILVER3,
    SILVER2,
    SILVER1,
    ;

    @JsonCreator
    public static ProblemLevelType parsing(String value) {
        return Stream.of(ProblemLevelType.values())
                .filter(v -> v.name().equals(value))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }
}
