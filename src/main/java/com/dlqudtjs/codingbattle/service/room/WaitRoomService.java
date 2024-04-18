package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.WaitRoomCreateRequestDto;

public interface WaitRoomService {

    ResponseDto createWaitRoom(WaitRoomCreateRequestDto requestDto, String token);

    ResponseDto enterWaitRoom(WaitRoomCreateRequestDto requestDto);
}
