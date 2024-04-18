package com.dlqudtjs.codingbattle.service.room;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.WaitRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.WaitRoomEnterRequestDto;

public interface RoomService {

    ResponseDto createWaitRoom(WaitRoomCreateRequestDto requestDto, String token);

    ResponseDto enterWaitRoom(WaitRoomEnterRequestDto requestDto, String token);

    ResponseDto leaveWaitRoom(String userId, Integer roomId);
}
