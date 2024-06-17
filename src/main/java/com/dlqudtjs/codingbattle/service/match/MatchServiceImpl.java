package com.dlqudtjs.codingbattle.service.match;

import static com.dlqudtjs.codingbattle.common.constant.MatchingResultType.DRAW;
import static com.dlqudtjs.codingbattle.common.constant.MatchingResultType.LOSE;
import static com.dlqudtjs.codingbattle.common.constant.MatchingResultType.PENDING;
import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.util.Time;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.game.Winner;
import com.dlqudtjs.codingbattle.entity.match.MatchHistory;
import com.dlqudtjs.codingbattle.entity.match.MatchRecode;
import com.dlqudtjs.codingbattle.entity.match.MatchRecodeUserStatus;
import com.dlqudtjs.codingbattle.entity.match.UserMatchingHistory;
import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.repository.game.MatchHistoryRepository;
import com.dlqudtjs.codingbattle.repository.game.UserMatchingHistoryRepository;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchHistoryRepository matchHistoryRepository;
    private final UserMatchingHistoryRepository userMatchingHistoryRepository;

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

            UserMatchingHistory userMatchingHistory = userMatchingHistoryRepository.findByMatchHistoryIdAndUserId(
                    gameSession.getMatchId(), user.getId());

            userMatchingHistory.updateResult(matchingResultType);
        });

        matchHistoryRepository.findById(gameSession.getMatchId())
                .ifPresent(MatchHistory::matchEnd);
    }

    @Override
    public List<MatchRecode> getMatchRecodeList(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MatchHistory> matchHistories =
                userMatchingHistoryRepository.findMatchHistoriesByUserId(user.getId(), pageable);

        return matchHistories.getContent().stream().map(matchHistory -> MatchRecode.builder()
                .usersResult(getMatchRecodeUserStatuses(matchHistory.getId()))
                .matchId(matchHistory.getId())
                .language(matchHistory.getLanguage())
                .result(getResultType(user, getMatchRecodeUserStatuses(matchHistory.getId())))
                .problemLevel(matchHistory.getProblemLevel())
                .elapsedTime(Time.getElapsedTime(matchHistory.getStartTime(), matchHistory.getEndTime()))
                .date(Time.convertTimestampToZonedDateTime(matchHistory.getStartTime()))
                .build()).toList();
    }

    private MatchingResultType getResultType(User user, List<MatchRecodeUserStatus> matchRecodeUserStatuses) {
        return matchRecodeUserStatuses.stream()
                .filter(matchRecodeUserStatus -> matchRecodeUserStatus.getUser().equals(user))
                .findFirst()
                .map(MatchRecodeUserStatus::getResult)
                .orElse(null);
    }


    private List<MatchRecodeUserStatus> getMatchRecodeUserStatuses(Long matchHistoryId) {
        List<UserMatchingHistory> userMatchingHistories = userMatchingHistoryRepository.findByMatchHistoryId(
                matchHistoryId);

        return userMatchingHistories.stream().map(userMatchingHistory -> MatchRecodeUserStatus.builder()
                .user(userMatchingHistory.getUser())
                .result(userMatchingHistory.getResult())
                .build()).toList();
    }

    private MatchHistory saveMatch(GameSession gameSession) {
        MatchHistory matchHistory = saveMatchHistory(gameSession);
        saveUserMatchingHistory(gameSession, matchHistory);

        return matchHistory;
    }

    private MatchHistory saveMatchHistory(GameSession gameSession) {
        ProblemLevel problemLevel = gameSession.getGameRunningConfig().getProblemLevel();

        return matchHistoryRepository.save(MatchHistory.builder()
                .problemLevel(problemLevel)
                .startTime(new Timestamp(gameSession.getStartTime()))
                .language(gameSession.getGameRunningConfig().getLanguage())
                .build());
    }

    private void saveUserMatchingHistory(GameSession gameSession, MatchHistory matchHistory) {
        for (User user : gameSession.getGameUserList()) {
            userMatchingHistoryRepository.save(UserMatchingHistory.builder()
                    .user(user)
                    .matchHistory(matchHistory)
                    .result(PENDING)
                    .build());
        }
    }
}
