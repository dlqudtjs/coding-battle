package com.dlqudtjs.codingbattle.model.room.requestDto;

import com.dlqudtjs.codingbattle.model.room.WaitRoom;
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
public class WaitRoomCreateRequestDto {

    @NotBlank
    private String hostId;

    @NotBlank
    private String title;

    private String password;

    @NotBlank
    private String language;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer problemLevel;

    @NotNull
    @Min(1)
    @Max(4)
    private Integer maxUserCount;
    
    @NotNull
    @Min(1)
    @Max(10)
    private Integer maxSummitCount;

    @NotNull
    @Min(10)
    @Max(120)
    private Integer limitTime;

    public WaitRoom toEntity() {
        return WaitRoom.builder()
                .hostId(hostId)
                .title(title)
                .password(password)
                .language(language)
                .problemLevel(problemLevel)
                .maxUserCount(maxUserCount)
                .maxSummitCount(maxSummitCount)
                .limitTime(limitTime)
                .userMap(new ConcurrentHashMap<>())
                .build();
    }
}
