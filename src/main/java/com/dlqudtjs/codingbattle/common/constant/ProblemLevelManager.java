package com.dlqudtjs.codingbattle.common.constant;

import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import com.dlqudtjs.codingbattle.repository.problem.ProblemLevelRepository;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProblemLevelManager {

    private static final Map<String, ProblemLevel> LEVELS = new HashMap<>();
    private final ProblemLevelRepository problemLevelRepository;

    public static ProblemLevel BRONZE5;
    public static ProblemLevel BRONZE4;
    public static ProblemLevel BRONZE3;
    public static ProblemLevel BRONZE2;
    public static ProblemLevel BRONZE1;
    public static ProblemLevel SILVER5;
    public static ProblemLevel SILVER4;
    public static ProblemLevel SILVER3;
    public static ProblemLevel SILVER2;
    public static ProblemLevel SILVER1;

    @PostConstruct
    private void init() {
        problemLevelRepository.findAll().forEach(problemLevel -> {
            LEVELS.put(problemLevel.getName(), problemLevel);
        });
        setLevel();
    }

    public static ProblemLevel getProblemLevelFromName(String levelName) {
        return LEVELS.get(levelName.toUpperCase());
    }

    public static Boolean isSupportedLevel(String levelName) {
        return LEVELS.containsKey(levelName.toUpperCase());
    }

    private void setLevel() {
        BRONZE5 = getProblemLevel("BRONZE5");
        BRONZE4 = getProblemLevel("BRONZE4");
        BRONZE3 = getProblemLevel("BRONZE3");
        BRONZE2 = getProblemLevel("BRONZE2");
        BRONZE1 = getProblemLevel("BRONZE1");
        SILVER5 = getProblemLevel("SILVER5");
        SILVER4 = getProblemLevel("SILVER4");
        SILVER3 = getProblemLevel("SILVER3");
        SILVER2 = getProblemLevel("SILVER2");
        SILVER1 = getProblemLevel("SILVER1");
    }

    private ProblemLevel getProblemLevel(String levelName) {
        ProblemLevel level = LEVELS.get(levelName);
        if (level == null) {
            throw new IllegalArgumentException("Invalid level name: " + levelName);
        }
        return level;
    }
}
