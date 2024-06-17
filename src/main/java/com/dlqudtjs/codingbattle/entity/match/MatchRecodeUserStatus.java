package com.dlqudtjs.codingbattle.entity.match;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.entity.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchRecodeUserStatus {

    private User user;
    private MatchingResultType result;
}
