package com.dlqudtjs.codingbattle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodingbattleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodingbattleApplication.class, args);
    }

    // TODO : 전적 검색시 우승자 코드를 확인하는 기능은 어떻게 구현할 것인가 의논
    // TODO: 단순 나열 테이블 -> enum 으로 대체하기
}
