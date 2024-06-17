package com.dlqudtjs.codingbattle.common.constant;

import com.dlqudtjs.codingbattle.entity.submit.SubmitResult;
import com.dlqudtjs.codingbattle.repository.game.SubmitResultRepository;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SubmitResultManager {

    private static final Map<String, SubmitResult> RESULTS = new HashMap<>();
    private final SubmitResultRepository submitResultRepository;

    public static SubmitResult PENDING;
    public static SubmitResult PASS;
    public static SubmitResult FAIL;
    public static SubmitResult ERROR;

    @PostConstruct
    private void init() {
        submitResultRepository.findAll().forEach(submitResult -> {
            RESULTS.put(submitResult.getName(), submitResult);
        });
        setResult();
    }

    public static Boolean isPass(SubmitResult submitResult) {
        return PASS.equals(submitResult);
    }

    public static SubmitResult getSubmitResultFromName(String result) {
        return RESULTS.get(result.toUpperCase());
    }

    public static Boolean isValid(String result) {
        return RESULTS.containsKey(result.toUpperCase());
    }

    private void setResult() {
        PENDING = getResult("PENDING");
        PASS = getResult("PASS");
        FAIL = getResult("FAIL");
        ERROR = getResult("ERROR");
    }

    private SubmitResult getResult(String resultName) {
        SubmitResult result = RESULTS.get(resultName);
        if (result == null) {
            throw new IllegalArgumentException("Invalid result name: " + resultName);
        }
        return result;
    }
}
