package com.dlqudtjs.codingbattle.security;

import com.dlqudtjs.codingbattle.repository.user.UserRepository;
import com.dlqudtjs.codingbattle.common.exception.oauth.OauthErrorCode;
import com.dlqudtjs.codingbattle.common.exception.oauth.UserIdNotFoundException;
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
                .orElseThrow(() -> new UserIdNotFoundException(OauthErrorCode.USER_ID_NOT_FOUNT.getMessage()));
    }
}
