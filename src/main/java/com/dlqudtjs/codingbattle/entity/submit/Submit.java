package com.dlqudtjs.codingbattle.entity.submit;

import com.dlqudtjs.codingbattle.dto.game.requestDto.UpdateSubmitResultRequestDto;
import com.dlqudtjs.codingbattle.entity.game.MatchHistory;
import com.dlqudtjs.codingbattle.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "submit")
public class Submit implements Comparable<Submit> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "submit_result_code_id", nullable = false)
    private SubmitResultCode submitResultCode;

    @OneToOne
    @JoinColumn(name = "match_history_id", nullable = false)
    private MatchHistory matchHistory;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "memory")
    private Long memory;

    @Column(name = "execution_time")
    private Long executionTime;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "submit_time", nullable = false)
    private Date submitTime;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public String getCode() {
        return code;
    }

    public SubmitResultCode getSubmitResultCode() {
        return submitResultCode;
    }

    public void updateSubmitResult(UpdateSubmitResultRequestDto updateSubmitResultRequestDto) {
        this.executionTime = updateSubmitResultRequestDto.getExecutionTime();
        this.submitResultCode = updateSubmitResultRequestDto.getSubmitResultCode();
    }

    @Override
    public int compareTo(Submit o) {
        return this.submitTime.compareTo(o.submitTime);
    }
}
