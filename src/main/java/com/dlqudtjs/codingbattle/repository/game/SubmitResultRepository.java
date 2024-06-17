package com.dlqudtjs.codingbattle.repository.game;

import com.dlqudtjs.codingbattle.entity.submit.SubmitResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmitResultRepository extends JpaRepository<SubmitResult, Long> {

    SubmitResult findByName(String name);
}
