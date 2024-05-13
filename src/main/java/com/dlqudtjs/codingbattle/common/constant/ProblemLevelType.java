package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProblemLevelType {
    BRONZE1(1),
    BRONZE2(2),
    BRONZE3(3),
    BRONZE4(4),
    BRONZE5(5),
    SILVER1(6),
    SILVER2(7),
    SILVER3(8),
    SILVER4(9),
    SILVER5(10),
    ;

    private final int value;

    public static ProblemLevelType getProblemLevel(int value) {
        for (ProblemLevelType pl : ProblemLevelType.values()) {
            if (pl.getValue() == value) {
                return pl;
            }
        }

        return null;
    }
}
