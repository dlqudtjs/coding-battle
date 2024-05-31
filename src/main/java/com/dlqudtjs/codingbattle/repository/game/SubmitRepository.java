package com.dlqudtjs.codingbattle.repository.game;

import com.dlqudtjs.codingbattle.entity.submit.Submit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SubmitRepository extends JpaRepository<Submit, Long> {

    @Modifying
    @Query("UPDATE Submit s SET s.executionTime = :execution_time, s.submitResultCode.id = :submit_result_code_id WHERE s.id = :submit_id")
    @Transactional
    int updateSubmitResult(@Param("submit_id") Long submit_id,
                           @Param("execution_time") Long execution_time,
                           @Param("submit_result_code_id") Long submit_result_code_id);
}
