package com.dlqudtjs.codingbattle.dto.score;

import com.dlqudtjs.codingbattle.entity.match.MatchRecode;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScoreListResponseDto {

    private List<MatchRecode> matchRecodeList;
    private Integer currentPage;
    private Integer totalPage;
}
