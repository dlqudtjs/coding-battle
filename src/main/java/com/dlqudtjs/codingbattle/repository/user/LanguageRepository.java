package com.dlqudtjs.codingbattle.repository.user;

import com.dlqudtjs.codingbattle.model.user.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Language findByName(String name);
}
