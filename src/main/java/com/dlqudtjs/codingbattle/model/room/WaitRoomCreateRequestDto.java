package com.dlqudtjs.codingbattle.model.room;

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

    private String hostId;
    private String title;
    private String password;
    private String language;
    private int problemLevel;
    private int maxUserCount;
    private int maxSummitCount;
    private int limitTime;

    public WaitRoom toEntity() {
        return WaitRoom.builder()
                .hostId(hostId)
                .title(title)
                .password(password)
                .language(language)
                .problemLevel(problemLevel)
                .maxUserCount(maxUserCount)
                .maxSummitCount(maxSummitCount)
                .userMap(new ConcurrentHashMap<>())
                .build();
    }
}
