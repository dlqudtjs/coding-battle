package com.dlqudtjs.codingbattle.service.match;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.MatchHistory;
import com.dlqudtjs.codingbattle.entity.game.MatchingResultClassification;
import com.dlqudtjs.codingbattle.entity.game.UserMatchingHistory;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.repository.game.MatchHistoryRepository;
import com.dlqudtjs.codingbattle.repository.game.MatchingResultClassificationRepository;
import com.dlqudtjs.codingbattle.repository.game.UserMatchingHistoryRepository;
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

    private MatchHistory saveMatch(GameSession gameSession) {
        MatchHistory matchHistory = saveMatchHistory(gameSession);
        saveUserMatchingHistory(gameSession, matchHistory);

        return matchHistory;
    }

    private MatchHistory saveMatchHistory(GameSession gameSession) {
        return matchHistoryRepository.save(MatchHistory.builder()
                .startTime(gameSession.getStartTime())
                .build());
    }

    private void saveUserMatchingHistory(GameSession gameSession, MatchHistory matchHistory) {
        MatchingResultClassification matchingResultClassification =
                getMatchingResultClassification(MatchingResultType.PENDING);

        for (User user : gameSession.getGameRoom().getUserList()) {
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
