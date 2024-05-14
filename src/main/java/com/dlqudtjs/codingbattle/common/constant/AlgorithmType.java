package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlgorithmType {

    GREEDY("Greedy"),
    BINARY_SEARCH("Binary Search"),
    IMPLEMENTATION("Implementation"),
    DYNAMIC_PROGRAMMING("Dynamic Programming"),
    BRUTE_FORCE("Brute Force"),
    ;

    private final String value;
}
