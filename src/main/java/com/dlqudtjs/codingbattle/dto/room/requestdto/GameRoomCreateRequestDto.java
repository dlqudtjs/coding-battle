package com.dlqudtjs.codingbattle.dto.room.requestdto;

import static com.dlqudtjs.codingbattle.common.exception.CommonErrorCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomCreateRequestDto {

    @NotBlank
    private String hostId;

    @NotBlank
    private String title;

    private String password;

    @NotBlank
    private String language;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer problemLevel;

    @NotNull
    @Min(2)
    @Max(4)
    private Integer maxUserCount;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer maxSubmitCount;

    @NotNull
    @Min(10)
    @Max(120)
    private Integer limitTime;

    public GameRoom toEntity() {
        return GameRoom.builder()
                .hostId(hostId)
                .title(title)
                .password(password)
                .language(validateLanguage(language))
                .isStarted(false)
                .problemLevel(ProblemLevelType.getProblemLevel(problemLevel))
                .maxUserCount(maxUserCount)
                .maxSubmitCount(maxSubmitCount)
                .limitTime(limitTime)
                .userMap(new ConcurrentHashMap<>())
                .build();
    }

    private ProgrammingLanguage validateLanguage(String language) {
        for (ProgrammingLanguage pl : ProgrammingLanguage.values()) {
            if (pl.getLanguageName().equals(language)) {
                return pl;
            }
        }

        throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
    }
}
