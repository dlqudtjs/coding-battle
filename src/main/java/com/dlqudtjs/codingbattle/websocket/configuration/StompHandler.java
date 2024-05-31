//package com.dlqudtjs.codingbattle.websocket.configuration;
//
//import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
//import com.dlqudtjs.codingbattle.service.room.RoomService;
//import com.dlqudtjs.codingbattle.service.session.SessionService;
//import com.dlqudtjs.codingbattle.service.user.UserService;
//import lombok.NonNull;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
//@Slf4j
//public class StompHandler implements ChannelInterceptor {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final SessionService sessionService;
//    private final UserService userService;
//    private final RoomService roomService;
//
//    @Override
//    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
//
//        return message;
//    }
//}
