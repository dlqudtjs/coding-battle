package com.dlqudtjs.codingbattle.common.constant;

import com.dlqudtjs.codingbattle.entity.match.MatchResult;
import com.dlqudtjs.codingbattle.repository.game.MatchResultRepository;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MatchResultManager {

    private static final Map<String, MatchResult> MATCH_RESULTS = new HashMap<>();
    private final MatchResultRepository matchResultRepository;

    public static MatchResult PENDING;
    public static MatchResult WIM;
    public static MatchResult DRAW;
    public static MatchResult LOSE;

    @PostConstruct
    private void init() {
        matchResultRepository.findAll().forEach(matchResult ->
                MATCH_RESULTS.put(matchResult.getName(), matchResult));

        setMatchResult();
    }

    public static Boolean isValid(String matchResult) {
        return MATCH_RESULTS.containsKey(matchResult.toUpperCase());
    }

    private void setMatchResult() {
        PENDING = getMatchResult("PENDING");
        WIM = getMatchResult("WIN");
        DRAW = getMatchResult("DRAW");
        LOSE = getMatchResult("LOSE");
    }

    private MatchResult getMatchResult(String matchResultName) {
        MatchResult matchResult = MATCH_RESULTS.get(matchResultName);
        if (matchResult == null) {
            throw new IllegalArgumentException("Invalid match result name: " + matchResultName);
        }
        return matchResult;
    }
}
