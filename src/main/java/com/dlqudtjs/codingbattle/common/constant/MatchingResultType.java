package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchingResultType {
    PENDING(0L),
    PERFECT_WIN(1L),
    WIN(2L),
    DRAW(3L),
    LOSE(4L);

    private final Long value;
}
