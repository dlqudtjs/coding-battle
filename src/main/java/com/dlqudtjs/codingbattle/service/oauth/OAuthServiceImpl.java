package com.dlqudtjs.codingbattle.service.oauth;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.oauth.JwtToken;
import com.dlqudtjs.codingbattle.model.oauth.JwtTokenDto;
import com.dlqudtjs.codingbattle.model.oauth.SignInRequestDto;
import com.dlqudtjs.codingbattle.model.oauth.SignUpRequestDto;
import com.dlqudtjs.codingbattle.model.user.User;
import com.dlqudtjs.codingbattle.repository.TokenRepository;
import com.dlqudtjs.codingbattle.repository.UserRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.oauth.exception.AlreadyExistNicknameException;
import com.dlqudtjs.codingbattle.service.oauth.exception.AlreadyExistUserIdException;
import com.dlqudtjs.codingbattle.service.oauth.exception.ErrorCode;
import com.dlqudtjs.codingbattle.service.oauth.exception.PasswordCheckException;
import com.dlqudtjs.codingbattle.service.oauth.exception.PasswordNotMatchException;
import com.dlqudtjs.codingbattle.service.oauth.exception.UserIdNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public ResponseDto signIn(SignInRequestDto signInRequestDto) {
        User user = userRepository.findByUserId(signInRequestDto.getId())
                .orElseThrow(() -> new UserIdNotFoundException(ErrorCode.USER_ID_NOT_FOUNT.getMessage()));

        validateSignInRequest(signInRequestDto, user);

        // 1. user_id + password를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                signInRequestDto.getId(), signInRequestDto.getPassword()
        );

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 User에 대한 검증 진행
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(authentication);

        saveRefreshToken(jwtTokenDto.getRefreshToken(), user.getId());

        return ResponseDto.builder()
                .status(SuccessCode.SIGN_IN_SUCCESS.getStatus())
                .message(SuccessCode.SIGN_IN_SUCCESS.getMessage())
                // 3. authentication 객체로 jwt 토큰 생성
                .data(jwtTokenDto)
                .build();
    }

    private void saveRefreshToken(String refreshToken, Long userId) {
        Optional<JwtToken> jwtToken = tokenRepository.findByUserId(userId);

        if (jwtToken.isPresent()) {
            jwtToken.get().setRefreshToken(refreshToken);
        } else {
            tokenRepository.save(JwtToken.builder()
                    .userId(userId)
                    .refreshToken(refreshToken)
                    .build());
        }
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

    private void validateSignInRequest(SignInRequestDto signInRequestDto, User user) {
        if (passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException(ErrorCode.PASSWORD_NOT_MATCH.getMessage());
        }
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
