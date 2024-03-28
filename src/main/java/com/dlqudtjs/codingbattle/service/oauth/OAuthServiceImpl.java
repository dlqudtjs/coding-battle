package com.dlqudtjs.codingbattle.service.oauth;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.oauth.SignInRequestDto;
import com.dlqudtjs.codingbattle.model.oauth.SignUpRequestDto;
import com.dlqudtjs.codingbattle.model.user.User;
import com.dlqudtjs.codingbattle.repository.JpaUserRepository;
import com.dlqudtjs.codingbattle.service.oauth.exception.AlreadyExistUserIdException;
import com.dlqudtjs.codingbattle.service.oauth.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    private final JpaUserRepository userRepository;
    @Override
    public ResponseDto signIn(SignInRequestDto signInRequestDto) {
        return null;
    }

    @Override
    public ResponseDto singUp(SignUpRequestDto signUpRequestDto) {
        validateSignUpRequest(signUpRequestDto);

        User user = User.builder()
                .userId(signUpRequestDto.getUserId())
                .nickname(signUpRequestDto.getNickname())
                .password(signUpRequestDto.getPassword())
                .build();

        userRepository.save(user);

        return null;
    }

    private void validateSignUpRequest(SignUpRequestDto signUpRequestDto) {
        if (isExistUser(signUpRequestDto.getUserId())) {
            return;
        }

        if (isExistNickname(signUpRequestDto.getNickname())) {
            return;
        }

        if (!passwordCheck(signUpRequestDto.getPassword(), signUpRequestDto.getPasswordCheck())) {
            return;
        }

        throw new AlreadyExistUserIdException(ErrorCode.ALREADY_EXIST_USER_ID.getMessage());
    }

    private boolean isExistUser(String userId) {
        return userRepository.existsByUserId(userId);
    }

    private boolean isExistNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    private boolean passwordCheck(String password, String passwordCheck) {
        return password.equals(passwordCheck);
    }
}
