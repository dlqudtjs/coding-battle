package com.dlqudtjs.codingbattle.dto.room.requestdto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomEnterRequestDto {

    @NotNull
    private Long roomId;

    @NotNull
    private String userId;

    @NotNull
    private String password;
}
