package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchingResultType {
    PENDING(0L),
    WIN(1L),
    DRAW(2L),
    LOSE(3L);

    private final Long value;
}
