package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.entity.match.MatchRecode;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.service.match.MatchService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
            @RequestParam(required = false, defaultValue = "0", value = "pageNo") int page,
            @RequestParam(required = false, defaultValue = "10", value = "size") int size) {
        User user = userService.getUser(userId);

        List<MatchRecode> matchRecodeList = matchService.getMatchRecodeList(user, page, size);

        return ResponseEntity.ok().body(ResponseDto.builder()
                .data(matchRecodeList)
                .build());
    }
}
