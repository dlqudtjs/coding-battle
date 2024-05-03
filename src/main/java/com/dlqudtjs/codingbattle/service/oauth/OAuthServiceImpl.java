package com.dlqudtjs.codingbattle.service.oauth;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.model.oauth.JwtToken;
import com.dlqudtjs.codingbattle.model.oauth.JwtTokenDto;
import com.dlqudtjs.codingbattle.model.oauth.SignInRequestDto;
import com.dlqudtjs.codingbattle.model.oauth.SignUpRequestDto;
import com.dlqudtjs.codingbattle.model.user.User;
import com.dlqudtjs.codingbattle.repository.token.TokenRepository;
import com.dlqudtjs.codingbattle.repository.user.UserRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.service.oauth.exception.AlreadyExistUserIdException;
import com.dlqudtjs.codingbattle.service.oauth.exception.CustomAuthenticationException;
import com.dlqudtjs.codingbattle.service.oauth.exception.ErrorCode;
import com.dlqudtjs.codingbattle.service.oauth.exception.PasswordCheckException;
import com.dlqudtjs.codingbattle.service.oauth.exception.PasswordNotMatchException;
import com.dlqudtjs.codingbattle.service.oauth.exception.UserIdNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public ResponseDto signIn(SignInRequestDto signInRequestDto) {
        User user = userRepository.findByUserId(signInRequestDto.getUserId())
                .orElseThrow(() -> new UserIdNotFoundException(ErrorCode.USER_ID_NOT_FOUNT.getMessage()));

        validateSignInRequest(signInRequestDto, user);

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(user);

        saveRefreshToken(jwtTokenDto.getRefreshToken(), user.getId());

        return ResponseDto.builder()
                .status(SuccessCode.SIGN_IN_SUCCESS.getStatus())
                .message(SuccessCode.SIGN_IN_SUCCESS.getMessage())
                .data(jwtTokenDto)
                .build();
    }


    @Override
    @Transactional
    public ResponseDto singUp(SignUpRequestDto signUpRequestDto) {
        validateSignUpRequest(signUpRequestDto);

        User savedUser = userRepository.save(signUpRequestDto.toEntity()
                .encodePassword(passwordEncoder));

        return ResponseDto.builder()
                .status(SuccessCode.SIGN_UP_SUCCESS.getStatus())
                .message(SuccessCode.SIGN_UP_SUCCESS.getMessage())
                .data(savedUser.getId())
                .build();
    }

    @Override
    @Transactional
    public ResponseDto refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        User user = userRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new CustomAuthenticationException(ErrorCode.USER_ID_NOT_FOUNT.getMessage()));

        JwtToken jwtToken = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomAuthenticationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage()));

        // Refresh Token이 일치하지 않을 경우
        if (!jwtToken.getRefreshToken().equals(refreshToken.substring(7))) {
            throw new CustomAuthenticationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(user);

        saveRefreshToken(jwtTokenDto.getRefreshToken(), user.getId());

        return ResponseDto.builder()
                .status(SuccessCode.REFRESH_TOKEN_SUCCESS.getStatus())
                .message(SuccessCode.REFRESH_TOKEN_SUCCESS.getMessage())
                .data(jwtTokenDto)
                .build();
    }

    @Override
    @Transactional
    public ResponseDto checkUserId(String userId) {
        if (isExistUser(userId)) {
            throw new AlreadyExistUserIdException(ErrorCode.ALREADY_EXIST_USER_ID.getMessage());
        }

        return ResponseDto.builder()
                .status(SuccessCode.CHECK_USER_ID_SUCCESS.getStatus())
                .message(SuccessCode.CHECK_USER_ID_SUCCESS.getMessage())
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

    private void validateSignInRequest(SignInRequestDto signInRequestDto, User user) {
        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException(ErrorCode.PASSWORD_NOT_MATCH.getMessage());
        }
    }

    private void validateSignUpRequest(SignUpRequestDto signUpRequestDto) {
        if (isExistUser(signUpRequestDto.getUserId())) {
            throw new AlreadyExistUserIdException(ErrorCode.ALREADY_EXIST_USER_ID.getMessage());
        }

        if (!passwordCheck(signUpRequestDto.getPassword(), signUpRequestDto.getPasswordCheck())) {
            throw new PasswordCheckException(ErrorCode.PASSWORD_CHECK.getMessage());
        }
    }

    private boolean isExistUser(String userId) {
        return userRepository.existsByUserId(userId);
    }

    private boolean passwordCheck(String password, String passwordCheck) {
        return password.equals(passwordCheck);
    }
}
