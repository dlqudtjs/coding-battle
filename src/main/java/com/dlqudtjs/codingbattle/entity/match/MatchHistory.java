package com.dlqudtjs.codingbattle.entity.match;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.util.Time;
import com.dlqudtjs.codingbattle.entity.problem.ProblemLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "match_history")
public class MatchHistory {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_level_id", nullable = false)
    private ProblemLevel problemLevelId;

    @Getter
    @Column(name = "start_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp startTime;
    

    @Getter
    @Column(name = "end_time")
    private Timestamp endTime;

    @Getter
    @Column(name = "language", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;

    public void matchEnd() {
        this.endTime = Time.getTimestamp();
    }
}
