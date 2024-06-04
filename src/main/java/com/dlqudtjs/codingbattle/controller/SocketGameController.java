//package com.dlqudtjs.codingbattle.controller;
//
//import static com.dlqudtjs.codingbattle.common.constant.code.CommonConfigCode.INVALID_INPUT_VALUE;
//
//import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
//import com.dlqudtjs.codingbattle.dto.game.responseDto.SubmitDoneResponseDto;
//import com.dlqudtjs.codingbattle.dto.game.responseDto.messagewrapperdto.SubmitDoneMessageResponseDto;
//import com.dlqudtjs.codingbattle.entity.user.User;
//import com.dlqudtjs.codingbattle.service.game.GameService;
//import com.dlqudtjs.codingbattle.service.user.UserService;
//import com.dlqudtjs.codingbattle.websocket.configuration.WebsocketSessionHolder;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//@RequiredArgsConstructor
//public class SocketGameController {
//
//    private final SimpMessagingTemplate messagingTemplate;
//    private final GameService gameService;
//    private final UserService userService;
//
//    @MessageMapping("/game/{roomId}/{userId}/submitDone/")
//    public void submitDone(@DestinationVariable("roomId") Long roomId,
//                           @DestinationVariable("userId") String userId,
//                           SimpMessageHeaderAccessor headerAccessor) {
//        User user = userService.getUser(userId);
//        if (WebsocketSessionHolder.isMatched(headerAccessor.getSessionId(), user)) {
//            throw new Custom4XXException(INVALID_INPUT_VALUE.getMessage(), INVALID_INPUT_VALUE.getStatus());
//        }
//
//        Boolean submitDone = gameService.toggleSubmitDone(roomId, user);
//
//        SubmitDoneMessageResponseDto.builder()
//                .submitDone(SubmitDoneResponseDto.builder()
//                        .userId(userId)
//                        .submitDone(submitDone)
//                        .build());
//
//        messagingTemplate.convertAndSend("/topic/room/" + roomId, submitDone);
//    }
//}
