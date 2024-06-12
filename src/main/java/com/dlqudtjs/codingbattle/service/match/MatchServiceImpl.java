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
import com.dlqudtjs.codingbattle.entity.match.MatchingResultClassification;
import com.dlqudtjs.codingbattle.entity.match.UserMatchingHistory;
import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.repository.game.MatchHistoryRepository;
import com.dlqudtjs.codingbattle.repository.game.MatchingResultClassificationRepository;
import com.dlqudtjs.codingbattle.repository.game.UserMatchingHistoryRepository;
import com.dlqudtjs.codingbattle.repository.problem.ProblemLevelRepository;
import com.dlqudtjs.codingbattle.service.user.UserService;
import java.sql.Timestamp;
import java.util.ArrayList;
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
    private final MatchingResultClassificationRepository matchingResultClassificationRepository;
    private final ProblemLevelRepository problemLevelRepository;

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

    @Override
    public List<MatchRecode> getMatchRecodeList(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MatchHistory> matchHistories =
                userMatchingHistoryRepository.findMatchHistoriesByUserId(user.getId(), pageable);

        return matchHistories.getContent().stream().map(matchHistory -> MatchRecode.builder()
                .usersResult(getMatchRecodeUserStatuses(matchHistory.getId()))
                .matchId(matchHistory.getId())
                .language(matchHistory.getLanguage())
                // TODO: 조회한 사용자 기준으로 결과를 반환하는 로직 추가
                .result(matchHistory.getResult())
                .level(matchHistory.getLanguage())
                .elapsedTime(Time.getElapsedTime(matchHistory.getStartTime(), matchHistory.getEndTime()))
                .date(Time.getZonedDateTime(matchHistory.getStartTime()))
                .build()).toList();

    }

    private List<MatchRecodeUserStatus> getMatchRecodeUserStatuses(Long matchHistoryId) {
        List<UserMatchingHistory> userMatchingHistories = userMatchingHistoryRepository.findByMatchHistoryId(
                matchHistoryId);

        return userMatchingHistories.stream().map(userMatchingHistory -> MatchRecodeUserStatus.builder()
                .user(userMatchingHistory.getUser())
                .result(userMatchingHistory.getMatchingResultClassification())
                .build()).toList();
    }

    private MatchHistory saveMatch(GameSession gameSession) {
        MatchHistory matchHistory = saveMatchHistory(gameSession);
        saveUserMatchingHistory(gameSession, matchHistory);

        return matchHistory;
    }

    private MatchHistory saveMatchHistory(GameSession gameSession) {
        ProblemLevel problemLevel = problemLevelRepository.findByName(
                gameSession.getGameRunningConfig().getProblemLevel().name());

        return matchHistoryRepository.save(MatchHistory.builder()
                .problemLevelId(problemLevel)
                .startTime(new Timestamp(gameSession.getStartTime()))
                .language(gameSession.getGameRunningConfig().getLanguage())
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
