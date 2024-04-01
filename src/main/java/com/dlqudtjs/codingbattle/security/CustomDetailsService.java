package com.dlqudtjs.codingbattle.security;

import com.dlqudtjs.codingbattle.repository.UserRepository;
import com.dlqudtjs.codingbattle.service.oauth.exception.ErrorCode;
import com.dlqudtjs.codingbattle.service.oauth.exception.UserIdNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UserIdNotFoundException {

        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserIdNotFoundException(ErrorCode.USER_ID_NOT_FOUNT.getMessage()));
    }
}
