package com.dlqudtjs.codingbattle.entity.problem;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
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

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "problem")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "algorithm_classification_id", nullable = false)
    private AlgorithmClassification algorithmClassification;

    @JoinColumn(name = "problem_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProblemLevelType problemLevel;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "problem_description", nullable = false)
    private String problemDescription;

    @Column(name = "input_description", nullable = false)
    private String inputDescription;

    @Column(name = "output_description", nullable = false)
    private String outputDescription;

    @Column(name = "hint", nullable = false)
    private String hint;
}
