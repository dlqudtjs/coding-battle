package com.dlqudtjs.codingbattle.service.match;

import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.match.MatchHistory;
import com.dlqudtjs.codingbattle.entity.match.MatchRecodeUserStatus;
import com.dlqudtjs.codingbattle.entity.user.User;
import java.util.List;
import org.springframework.data.domain.Page;

public interface MatchService {

    MatchHistory startMatch(GameSession gameSession);

    MatchHistory getMatchHistory(Long matchId);

    void saveUserMatchHistory(GameSession gameSession, Winner winner);

    Page<MatchHistory> getMatchRecodeList(User user, int currentPage, int size);

    List<MatchRecodeUserStatus> getMatchRecodeUserStatuses(Long matchHistoryId);
}
