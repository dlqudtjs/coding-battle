package com.dlqudtjs.codingbattle.service.game;

import static com.dlqudtjs.codingbattle.common.exception.game.GameErrorCode.GAME_START_ERROR;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.room.CustomRoomException;
import com.dlqudtjs.codingbattle.common.exception.room.RoomErrorCode;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final RoomService roomService;

    @Override
    public ResponseDto startGame(GameStartRequestDto requestDto) {
        validateGameStartRequest(requestDto);

        // 모든 방이 레디되었는지 확인
        if (isNotGameStartable(requestDto.getRoomId())) {
            throw new Custom4XXException(GAME_START_ERROR.getMessage(), GAME_START_ERROR.getStatus());
        }

        return null;
    }

    private void validateGameStartRequest(GameStartRequestDto requestDto) {
        validateRoomExistence(requestDto.getRoomId());
    }

    // 방이 존재하지 않으면
    private void validateRoomExistence(Long roomId) {
        if (roomService.isExistRoom(roomId)) {
            throw new CustomRoomException(RoomErrorCode.NOT_EXIST_ROOM.getMessage());
        }
    }

    // 게임 시작할 수 없는지 확인
    private boolean isNotGameStartable(Long roomId) {
        if (!isAllUserReady(roomId)) {
            return true;
        }

        return false;
    }

    private boolean isAllUserReady(Long roomId) {
        return roomService.isAllUserReady(roomId);
    }
}
