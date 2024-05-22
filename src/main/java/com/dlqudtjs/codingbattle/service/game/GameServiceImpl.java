package com.dlqudtjs.codingbattle.service.game;

import com.dlqudtjs.codingbattle.common.constant.ProblemLevelType;
import com.dlqudtjs.codingbattle.common.constant.code.GameSuccessCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.room.CustomRoomException;
import com.dlqudtjs.codingbattle.common.exception.room.RoomErrorCode;
import com.dlqudtjs.codingbattle.dto.game.requestDto.GameStartRequestDto;
import com.dlqudtjs.codingbattle.entity.game.GameSession;
import com.dlqudtjs.codingbattle.entity.room.GameRoom;
import com.dlqudtjs.codingbattle.service.problem.ProblemService;
import com.dlqudtjs.codingbattle.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    static Map<Long, GameSession> gameSessionMap = new ConcurrentHashMap<>();
    private final RoomService roomService;
    private final ProblemService problemService;

    @Override
    public ResponseDto startGame(GameStartRequestDto requestDto) {
        validateGameStartRequest(requestDto);

        // 게임 시작
        GameRoom gameRoom = roomService.startGame(requestDto.getRoomId());

        // 난이도에 따른 문제 리스트 가져오기
        ProblemLevelType problemLevel = gameRoom.getProblemLevel();
        GameSession gameSession = GameSession.builder()
                .gameRoom(gameRoom)
                .problemList(problemService.getProblemList(null, problemLevel, 1))
                .build();

        gameSessionMap.put(requestDto.getRoomId(), gameSession);

        return ResponseDto.builder()
                .status(GameSuccessCode.GAME_START_SUCCESS.getStatus())
                .message(GameSuccessCode.GAME_START_SUCCESS.getMessage())
                .data(StartGameResponseDto.builder()
                        .problemList(gameSession.getProblemList())
                        .build())
                .build();
    }

    // 게임 시작 요청 검증
    private void validateGameStartRequest(GameStartRequestDto requestDto) {
        validateRoomExistence(requestDto.getRoomId());
    }

    // 방이 존재하지 않으면
    private void validateRoomExistence(Long roomId) {
        if (!roomService.isExistRoom(roomId)) {
            throw new CustomRoomException(RoomErrorCode.NOT_EXIST_ROOM.getMessage());
        }
    }
}
