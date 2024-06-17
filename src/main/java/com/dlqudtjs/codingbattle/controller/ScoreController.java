package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.util.Time;
import com.dlqudtjs.codingbattle.dto.score.ScoreListResponseDto;
import com.dlqudtjs.codingbattle.entity.match.MatchHistory;
import com.dlqudtjs.codingbattle.entity.match.MatchRecode;
import com.dlqudtjs.codingbattle.entity.match.MatchRecodeUserStatus;
import com.dlqudtjs.codingbattle.entity.match.MatchResult;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.service.match.MatchService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ScoreController {

    private final UserService userService;
    private final MatchService matchService;

    @GetMapping("/v1/recodes/{userId}")
    public ResponseEntity<ResponseDto> getRecodes(
            @PathVariable("userId") String userId,
            @RequestParam(required = false, defaultValue = "0", value = "pageNo") int currentPage,
            @RequestParam(required = false, defaultValue = "10", value = "size") int size) {
        User user = userService.getUser(userId);

        Page<MatchHistory> matchHistories = matchService.getMatchRecodeList(user, currentPage, size);

        List<MatchRecode> matchRecodes = matchHistories.getContent().stream()
                .map(matchHistory -> MatchRecode.builder()
                        .usersResult(matchService.getMatchRecodeUserStatuses(matchHistory.getId()))
                        .matchId(matchHistory.getId())
                        .language(matchHistory.getLanguage())
                        .result(getResultType(user, matchService.getMatchRecodeUserStatuses(matchHistory.getId())))
                        .problemLevel(matchHistory.getProblemLevel())
                        .elapsedTime(Time.getElapsedTime(matchHistory.getStartTime(), matchHistory.getEndTime()))
                        .date(Time.convertTimestampToZonedDateTime(matchHistory.getStartTime()))
                        .build()).toList();

        return ResponseEntity.ok().body(ResponseDto.builder()
                .data(ScoreListResponseDto.builder()
                        .matchRecodeList(matchRecodes)
                        .currentPage(currentPage)
                        .totalPage(matchHistories.getTotalPages())
                        .build())
                .build());
    }

    private MatchResult getResultType(User user, List<MatchRecodeUserStatus> matchRecodeUserStatuses) {
        return matchRecodeUserStatuses.stream()
                .filter(matchRecodeUserStatus -> matchRecodeUserStatus.getUser().equals(user))
                .findFirst()
                .map(MatchRecodeUserStatus::getResult)
                .orElse(null);
    }
}
