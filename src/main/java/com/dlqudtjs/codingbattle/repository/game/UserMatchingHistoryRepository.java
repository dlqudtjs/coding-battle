package com.dlqudtjs.codingbattle.repository.game;

import com.dlqudtjs.codingbattle.entity.match.MatchHistory;
import com.dlqudtjs.codingbattle.entity.match.UserMatchingHistory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserMatchingHistoryRepository extends JpaRepository<UserMatchingHistory, Long> {
    @Query("SELECT mh FROM MatchHistory mh "
            + "JOIN UserMatchingHistory umh ON mh.id = umh.matchHistoryId "
            + "WHERE umh.user.id = :userId ORDER BY mh.startTime DESC")
    Page<MatchHistory> findMatchHistoriesByUserId(@Param("userId") Long userId, Pageable pageable);

    List<UserMatchingHistory> findByMatchHistoryId(Long matchHistoryId);
}
