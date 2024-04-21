package com.dlqudtjs.codingbattle.model.room.requestDto;

import com.dlqudtjs.codingbattle.model.room.WaitRoom;
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
    private Integer problemLevel;
    private Integer maxUserCount;
    private Integer maxSummitCount;
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
