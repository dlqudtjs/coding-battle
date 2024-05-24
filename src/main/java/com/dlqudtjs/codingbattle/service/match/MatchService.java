package com.dlqudtjs.codingbattle.service.match;

import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.MatchHistory;

public interface MatchService {

    MatchHistory startMatch(GameSession gameSession);
}
