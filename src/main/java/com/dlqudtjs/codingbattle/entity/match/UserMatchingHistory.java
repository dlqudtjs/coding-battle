package com.dlqudtjs.codingbattle.entity.match;

import com.dlqudtjs.codingbattle.common.constant.MatchingResultType;
import com.dlqudtjs.codingbattle.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_matching_history")
public class UserMatchingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "match_history_id", nullable = false)
    private MatchHistory matchHistory;

    @Column(name = "matching_result", nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchingResultType result;

    public void updateResult(MatchingResultType result) {
        this.result = result;
    }
}
