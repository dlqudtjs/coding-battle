package com.dlqudtjs.codingbattle.dto.recode;

import static com.dlqudtjs.codingbattle.common.constant.MatchResultManager.DRAW;
import static com.dlqudtjs.codingbattle.common.constant.MatchResultManager.LOSE;
import static com.dlqudtjs.codingbattle.common.constant.MatchResultManager.WIM;

import java.util.List;
import lombok.Getter;

@Getter
public class ResultCountResponseDto {

    private Long total = 0L;
    private Long win = 0L;
    private Long lose = 0L;
    private Long draw = 0L;

    public ResultCountResponseDto(List<ResultCountDto> resultCounts) {
        resultCounts.forEach(resultCount -> {
            if (resultCount.getMatchResult().getName().equals(WIM.getName())) {
                this.win = resultCount.getCount();
                this.total += resultCount.getCount();
            } else if (resultCount.getMatchResult().getName().equals(LOSE.getName())) {
                this.lose = resultCount.getCount();
                this.total += resultCount.getCount();
            } else if (resultCount.getMatchResult().getName().equals(DRAW.getName())) {
                this.draw = resultCount.getCount();
                this.total += resultCount.getCount();
            }
        });
    }
}
