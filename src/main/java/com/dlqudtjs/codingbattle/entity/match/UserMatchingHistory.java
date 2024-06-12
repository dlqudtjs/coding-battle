package com.dlqudtjs.codingbattle.entity.match;

import com.dlqudtjs.codingbattle.entity.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_matching_history")
public class UserMatchingHistory {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @ManyToOne
    @JoinColumn(name = "match_history_id", nullable = false)
    private MatchHistory matchHistory;

    @Getter
    @ManyToOne
    @JoinColumn(name = "matching_result_classification_id", nullable = false)
    private MatchingResultClassification matchingResultClassification;
}
