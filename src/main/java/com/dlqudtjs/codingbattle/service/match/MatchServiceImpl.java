package com.dlqudtjs.codingbattle.service.match;

import static com.dlqudtjs.codingbattle.common.constant.MatchingResultType.*;
import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.match.MatchHistory;
import com.dlqudtjs.codingbattle.entity.match.MatchingResultClassification;
import com.dlqudtjs.codingbattle.entity.match.UserMatchingHistory;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.repository.game.MatchHistoryRepository;
import com.dlqudtjs.codingbattle.repository.game.MatchingResultClassificationRepository;
import com.dlqudtjs.codingbattle.repository.game.UserMatchingHistoryRepository;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchHistoryRepository matchHistoryRepository;
    private final UserMatchingHistoryRepository userMatchingHistoryRepository;
    private final MatchingResultClassificationRepository matchingResultClassificationRepository;

    @Override
    @Transactional
    public MatchHistory startMatch(GameSession gameSession) {
        return saveMatch(gameSession);
    }

    @Override
    public MatchHistory getMatchHistory(Long matchId) {
        return matchHistoryRepository.findById(matchId).orElseThrow(() -> new Custom4XXException(
                INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus()));
    }

    @Override
    @Transactional
    public void saveUserMatchHistory(GameSession gameSession, Winner winner) {
        gameSession.getGameUserList().forEach(user -> {
            MatchingResultType matchingResultType;

            if (winner.getMatchingResultType() == DRAW) {
                matchingResultType = DRAW;
            } else if (winner.equalsUser(user)) {
                matchingResultType = winner.getMatchingResultType();
            } else {
                matchingResultType = LOSE;
            }

            userMatchingHistoryRepository.save(UserMatchingHistory.builder()
                    .user(user)
                    .matchHistory(this.getMatchHistory(gameSession.getMatchId()))
                    .matchingResultClassification(getMatchingResultClassification(matchingResultType))
                    .build());
        });

        matchHistoryRepository.findById(gameSession.getMatchId())
                .ifPresent(MatchHistory::matchEnd);
    }

    private MatchHistory saveMatch(GameSession gameSession) {
        MatchHistory matchHistory = saveMatchHistory(gameSession);
        saveUserMatchingHistory(gameSession, matchHistory);

        return matchHistory;
    }

    private MatchHistory saveMatchHistory(GameSession gameSession) {
        return matchHistoryRepository.save(MatchHistory.builder()
                .startTime(new Timestamp(gameSession.getStartTime()))
                .build());
    }

    private void saveUserMatchingHistory(GameSession gameSession, MatchHistory matchHistory) {
        MatchingResultClassification matchingResultClassification =
                getMatchingResultClassification(PENDING);

        for (User user : gameSession.getGameUserList()) {
            userMatchingHistoryRepository.save(UserMatchingHistory.builder()
                    .user(user)
                    .matchHistory(matchHistory)
                    .matchingResultClassification(matchingResultClassification)
                    .build());
        }
    }

    private MatchingResultClassification getMatchingResultClassification(MatchingResultType matchingResultType) {
        return matchingResultClassificationRepository.findById(matchingResultType.getValue())
                .orElseThrow(RuntimeException::new);
    }
}
