package com.dlqudtjs.codingbattle.dto.recode;

import com.dlqudtjs.codingbattle.entity.match.MatchRecode;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecodeListResponseDto {

    private List<MatchRecode> matchRecodeList;
    private Integer currentPage;
    private Integer totalPage;
}
