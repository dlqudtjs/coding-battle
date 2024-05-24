package com.dlqudtjs.codingbattle.service.match;

import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.MatchHistory;
import com.dlqudtjs.codingbattle.repository.game.MatchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchHistoryRepository matchHistoryRepository;

    @Override
    public Long startMatch(GameSession gameSession) {
        MatchHistory savedMatchHistory = matchHistoryRepository.save(createMatchHistory(gameSession));
        return savedMatchHistory.getId();
    }

    private MatchHistory createMatchHistory(GameSession gameSession) {
        return MatchHistory.builder()
                .startTime(gameSession.getStartTime())
                .build();
    }
}
