package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProgrammingLanguage {

    DEFAULT("default"),
    JAVA("java"),
    PYTHON("python"),
    C("c"),
    CPP("cpp"),
    JAVASCRIPT("javascript"),
    ;

    private final String languageName;

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
