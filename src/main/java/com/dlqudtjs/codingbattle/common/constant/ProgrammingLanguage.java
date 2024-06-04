package com.dlqudtjs.codingbattle.common.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProgrammingLanguage {

    DEFAULT("default", "default", "default"),
    JAVA("java", "java", "Main.java"),
    PYTHON("python", "python", "main.py"),
    C("c", "gcc", "main.c"),
    JAVASCRIPT("javascript", "node", "app.js");

    private final String languageName;
    private final String dockerImageName;
    private final String fileName;

    public static ProgrammingLanguage getLanguage(String language) {
        for (ProgrammingLanguage pl : ProgrammingLanguage.values()) {
            if (pl.name().equals(language.toUpperCase())) {
                return pl;
            }
        }

        return DEFAULT;
    }

    public String getLanguageName() {
        return languageName;
    }

    public static boolean isNotContains(String language) {
        language = language.toUpperCase();

        for (ProgrammingLanguage pl : ProgrammingLanguage.values()) {
            if (pl.name().equals(language)) {
                return false;
            }
        }

        return true;
    }

    @JsonCreator
    public static ProgrammingLanguage parsing(String input) {
        return Stream.of(ProgrammingLanguage.values())
                .filter(language -> language.name().equals(input.toUpperCase()))
                .findFirst()
                .orElse(null);
    }
}
