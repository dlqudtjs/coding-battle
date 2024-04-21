package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.WaitRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.WaitRoomEnterRequestDto;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService waitRoomService;

    @PostMapping("/v1/waitRoom")
    public ResponseEntity<ResponseDto> createRoom(@RequestBody WaitRoomCreateRequestDto requestDto,
                                                  @RequestHeader("Authorization") String token) {
        ResponseDto responseDto = waitRoomService.createWaitRoom(requestDto, token);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping("/v1/waitRoom/enter")
    public ResponseEntity<ResponseDto> enterRoom(@RequestBody WaitRoomEnterRequestDto requestDto,
                                                 @RequestHeader("Authorization") String token) {
        ResponseDto responseDto = waitRoomService.enterWaitRoom(requestDto, token);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping("/v1/waitRoom/leave/{roomId}")
    public ResponseEntity<ResponseDto> leaveRoom(@PathVariable("roomId") Integer roomId,
                                                 @RequestHeader("Authorization") String token) {

        ResponseDto responseDto = waitRoomService.leaveWaitRoom(roomId, token);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }
}
