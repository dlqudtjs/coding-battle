package com.dlqudtjs.codingbattle.service.oauth;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.oauth.SignInRequestDto;
import com.dlqudtjs.codingbattle.model.oauth.SignUpRequestDto;
import com.dlqudtjs.codingbattle.model.user.User;
import com.dlqudtjs.codingbattle.repository.UserRepository;
import com.dlqudtjs.codingbattle.service.oauth.exception.AlreadyExistNicknameException;
import com.dlqudtjs.codingbattle.service.oauth.exception.AlreadyExistUserIdException;
import com.dlqudtjs.codingbattle.service.oauth.exception.ErrorCode;
import com.dlqudtjs.codingbattle.service.oauth.exception.PasswordCheckException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseDto signIn(SignInRequestDto signInRequestDto) {
        return null;
    }

    @Override
    @Transactional
    public ResponseDto singUp(SignUpRequestDto signUpRequestDto) {
        validateSignUpRequest(signUpRequestDto);

        User savedUser = userRepository.save(signUpRequestDto.toEntity().
                encodePassword(passwordEncoder));

        return ResponseDto.builder()
                .status(SuccessCode.SIGN_UP_SUCCESS.getStatus())
                .message(SuccessCode.SIGN_UP_SUCCESS.getMessage())
                .data(savedUser.getId())
                .build();
    }

    private void validateSignUpRequest(SignUpRequestDto signUpRequestDto) {
        if (isExistUser(signUpRequestDto.getUserId())) {
            throw new AlreadyExistUserIdException(ErrorCode.ALREADY_EXIST_USER_ID.getMessage());
        }

        if (isExistNickname(signUpRequestDto.getNickname())) {
            throw new AlreadyExistNicknameException(ErrorCode.ALREADY_EXIST_NICKNAME.getMessage());
        }

        if (!passwordCheck(signUpRequestDto.getPassword(), signUpRequestDto.getPasswordCheck())) {
            throw new PasswordCheckException(ErrorCode.PASSWORD_CHECK.getMessage());
        }
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
