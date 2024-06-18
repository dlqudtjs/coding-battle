package com.dlqudtjs.codingbattle.dto.recode;

import com.dlqudtjs.codingbattle.entity.match.MatchResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResultCountDto {
    private final MatchResult matchResult;
    private final Long count;
}
