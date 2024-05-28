package com.dlqudtjs.codingbattle.service.match;

import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.MatchHistory;
import com.dlqudtjs.codingbattle.entity.game.Winner;

public interface MatchService {

    MatchHistory startMatch(GameSession gameSession);

    MatchHistory getMatchHistory(Long matchId);

    void saveUserMatchHistory(GameSession gameSession, Winner winner);
}
