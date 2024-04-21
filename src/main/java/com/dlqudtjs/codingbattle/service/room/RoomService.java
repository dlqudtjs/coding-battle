package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.WaitRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.WaitRoomEnterRequestDto;

public interface RoomService {

    ResponseDto createWaitRoom(WaitRoomCreateRequestDto requestDto, String token);

    ResponseDto enterWaitRoom(WaitRoomEnterRequestDto requestDto, String token);

    ResponseDto leaveWaitRoom(Integer roomId, String token);
}
