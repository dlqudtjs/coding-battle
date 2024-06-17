package com.dlqudtjs.codingbattle.common.constant;

import com.dlqudtjs.codingbattle.entity.problem.Algorithm;
import com.dlqudtjs.codingbattle.repository.problem.AlgorithmRepository;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AlgorithmManager {

    private static final Map<String, Algorithm> LEVELS = new HashMap<>();
    private final AlgorithmRepository algorithmRepository;

    public static Algorithm GREEDY;
    public static Algorithm BINARY_SEARCH;
    public static Algorithm IMPLEMENTATION;
    public static Algorithm DYNAMIC_PROGRAMMING;
    public static Algorithm BRUTE_FORCE;

    @PostConstruct
    private void init() {
        algorithmRepository.findAll().forEach(algorithm -> {
            LEVELS.put(algorithm.getName(), algorithm);
        });
        setAlgorithm();
    }

    private void setAlgorithm() {
        GREEDY = getAlgorithm("GREEDY");
        BINARY_SEARCH = getAlgorithm("BINARY SEARCH");
        IMPLEMENTATION = getAlgorithm("IMPLEMENTATION");
        DYNAMIC_PROGRAMMING = getAlgorithm("DYNAMIC PROGRAMMING");
        BRUTE_FORCE = getAlgorithm("BRUTE FORCE");
    }

    private Algorithm getAlgorithm(String levelName) {
        Algorithm level = LEVELS.get(levelName);
        if (level == null) {
            throw new IllegalArgumentException("Invalid level name: " + levelName);
        }
        return level;
    }
}
