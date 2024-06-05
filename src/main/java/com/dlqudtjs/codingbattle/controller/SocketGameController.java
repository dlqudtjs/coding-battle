package com.dlqudtjs.codingbattle.controller;

import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;

import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.game.responseDto.UserSurrenderResponseDto;
import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.UserSurrenderMessageResponseDto;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.game.GameService;
import com.dlqudtjs.codingbattle.service.user.UserService;
import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SocketGameController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final GameService gameService;

    @MessageMapping("/games/{roomId}/{userId}/surrender")
    @SendTo("/topic/rooms/{roomId}")
    public UserSurrenderMessageResponseDto surrender(
            @DestinationVariable("roomId") Long roomId,
            @DestinationVariable("userId") String userId,
            SimpMessageHeaderAccessor headerAccessor) {

        User user = userService.getUser(jwtTokenProvider.getUserName(userId));
        User socketUser = WebsocketSessionHolder.getUserFromSessionId(headerAccessor.getSessionId());

        if (user != socketUser) {
            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
        }

        User surrenderUser = gameService.surrender(roomId, user);

        return UserSurrenderMessageResponseDto.builder()
                .userSurrender(UserSurrenderResponseDto.builder()
                        .userId(surrenderUser.getUserId())
                        .surrender(true)
                        .build())
                .build();
    }
}
