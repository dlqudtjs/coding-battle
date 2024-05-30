package com.dlqudtjs.codingbattle.security;

import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.USER_ID_NOT_FOUNT;

import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new Custom4XXException(
                        USER_ID_NOT_FOUNT.getMessage(),
                        USER_ID_NOT_FOUNT.getStatus()));
    }
}
