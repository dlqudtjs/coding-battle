package com.dlqudtjs.codingbattle.service.game;

import static com.dlqudtjs.codingbattle.common.exception.game.GameErrorCode.GAME_START_ERROR;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.room.CustomRoomException;
import com.dlqudtjs.codingbattle.common.exception.room.RoomErrorCode;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.repository.socket.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final RoomRepository roomRepository;

    @Override
    public ResponseDto startGame(GameStartRequestDto requestDto) {
        validateGameStartRequest(requestDto);

        return null;
    }

    private void validateGameStartRequest(GameStartRequestDto requestDto) {
        validateRoomExistence(requestDto.getRoomId());
    }

    // 방이 존재하지 않으면
    private void validateRoomExistence(Long roomId) {
        if (!roomRepository.isExistRoom(roomId)) {
            throw new CustomRoomException(RoomErrorCode.NOT_EXIST_ROOM.getMessage());
        }
    }

}
