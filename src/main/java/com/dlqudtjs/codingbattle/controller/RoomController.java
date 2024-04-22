package com.dlqudtjs.codingbattle.controller;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomCreateRequestDto;
import com.dlqudtjs.codingbattle.model.room.requestDto.GameRoomEnterRequestDto;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService gameRoomService;

    @PostMapping("/v1/gameRoom")
    public ResponseEntity<ResponseDto> createRoom(@Valid @RequestBody GameRoomCreateRequestDto requestDto,
                                                  @RequestHeader("Authorization") String token) {
        ResponseDto responseDto = gameRoomService.createGameRoom(requestDto, token);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping("/v1/gameRoom/enter")
    public ResponseEntity<ResponseDto> enterRoom(@RequestBody GameRoomEnterRequestDto requestDto,
                                                 @RequestHeader("Authorization") String token) {
        ResponseDto responseDto = gameRoomService.enterGameRoom(requestDto, token);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @PostMapping("/v1/gameRoom/leave/{roomId}")
    public ResponseEntity<ResponseDto> leaveRoom(@PathVariable("roomId") Integer roomId,
                                                 @RequestHeader("Authorization") String token) {

        ResponseDto responseDto = gameRoomService.leaveGameRoom(roomId, token);

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

    @GetMapping("/v1/gameRoomList")
    public ResponseEntity<ResponseDto> getGameRoomList() {
        ResponseDto responseDto = gameRoomService.getGameRoomList();

        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }
}
