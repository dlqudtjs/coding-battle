package com.dlqudtjs.codingbattle.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProgrammingLanguage {

    JAVA("java"),
    PYTHON("python"),
    C("c"),
    CPP("cpp"),
    JAVASCRIPT("javascript"),
    ;

    private final String languageName;
}
