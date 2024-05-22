package com.dlqudtjs.codingbattle.common.constant;

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

    public String getLanguageName() {
        return languageName;
    }

    public static boolean isNotContains(String language) {
        language = language.toLowerCase();

        for (ProgrammingLanguage pl : ProgrammingLanguage.values()) {
            if (pl.getLanguageName().equals(language)) {
                return false;
            }
        }
        return true;
    }
}
