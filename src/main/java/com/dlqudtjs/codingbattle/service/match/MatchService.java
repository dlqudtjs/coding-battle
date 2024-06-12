package com.dlqudtjs.codingbattle.service.match;

import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.match.MatchHistory;
import com.dlqudtjs.codingbattle.entity.match.MatchRecode;
import com.dlqudtjs.codingbattle.entity.user.User;
import java.util.List;

public interface MatchService {

    MatchHistory startMatch(GameSession gameSession);

    MatchHistory getMatchHistory(Long matchId);

    void saveUserMatchHistory(GameSession gameSession, Winner winner);

    List<MatchRecode> getMatchRecodeList(User user, int page, int size);
}
