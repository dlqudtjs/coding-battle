package com.dlqudtjs.codingbattle.repository.game;

import com.dlqudtjs.codingbattle.entity.submit.SubmitResultCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmitResultCodeRepository extends JpaRepository<SubmitResultCode, Long> {

    SubmitResultCode findByName(String name);
}
