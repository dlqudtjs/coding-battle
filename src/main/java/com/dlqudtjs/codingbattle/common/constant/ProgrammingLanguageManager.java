package com.dlqudtjs.codingbattle.common.constant;

import com.dlqudtjs.codingbattle.entity.user.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.repository.user.ProgrammingLanguageRepository;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProgrammingLanguageManager {

    private static final Map<String, ProgrammingLanguage> LANGUAGES = new HashMap<>();
    private final ProgrammingLanguageRepository programmingLanguageRepository;

    public static ProgrammingLanguage DEFAULT;
    public static ProgrammingLanguage JAVA;
    public static ProgrammingLanguage JAVASCRIPT;
    public static ProgrammingLanguage PYTHON;
    public static ProgrammingLanguage C;

    @PostConstruct
    private void init() {
        programmingLanguageRepository.findAll().forEach(programingLanguage -> {
            LANGUAGES.put(programingLanguage.getName(), programingLanguage);
        });

        setLanguage();
    }

    public static ProgrammingLanguage getLanguageFromName(String language) {
        return LANGUAGES.get(language.toUpperCase());
    }

    public static String getDockerImageName(ProgrammingLanguage language) {
        if (language == JAVA) {
            return "java";
        }

        return "default";
    }

    public static String getFileName(ProgrammingLanguage language) {
        if (language == JAVA) {
            return "Main.java";
        }

        return "default";
    }

    public static Boolean isSupportedLanguage(String language) {
        if (language == null) {
            return false;
        }

        return LANGUAGES.containsKey(language.toUpperCase());
    }

    private void setLanguage() {
        DEFAULT = getLanguage("DEFAULT");
        JAVA = getLanguage("JAVA");
        JAVASCRIPT = getLanguage("JAVASCRIPT");
        PYTHON = getLanguage("PYTHON");
        C = getLanguage("C");
    }

    private ProgrammingLanguage getLanguage(String languageName) {
        ProgrammingLanguage language = LANGUAGES.get(languageName);
        if (language == null) {
            throw new IllegalArgumentException("Invalid language name: " + languageName);
        }
        return language;
    }
}
