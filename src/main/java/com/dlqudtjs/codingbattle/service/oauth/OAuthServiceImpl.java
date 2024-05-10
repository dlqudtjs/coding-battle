package com.dlqudtjs.codingbattle.service.oauth;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.constant.UserRoleType;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.constant.OauthSuccessCode;
import com.dlqudtjs.codingbattle.entity.oauth.Token;
import com.dlqudtjs.codingbattle.dto.oauth.JwtTokenDto;
import com.dlqudtjs.codingbattle.dto.oauth.SignInRequestDto;
import com.dlqudtjs.codingbattle.dto.oauth.SignUpRequestDto;
import com.dlqudtjs.codingbattle.entity.user.Language;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserSetting;
import com.dlqudtjs.codingbattle.repository.token.TokenRepository;
import com.dlqudtjs.codingbattle.repository.user.LanguageRepository;
import com.dlqudtjs.codingbattle.repository.user.UserRepository;
import com.dlqudtjs.codingbattle.repository.user.UserRoleRepository;
import com.dlqudtjs.codingbattle.repository.user.UserSettingRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import com.dlqudtjs.codingbattle.common.exception.oauth.AlreadyExistUserIdException;
import com.dlqudtjs.codingbattle.common.exception.oauth.CustomAuthenticationException;
import com.dlqudtjs.codingbattle.common.exception.oauth.OauthErrorCode;
import com.dlqudtjs.codingbattle.common.exception.oauth.PasswordCheckException;
import com.dlqudtjs.codingbattle.common.exception.oauth.PasswordNotMatchException;
import com.dlqudtjs.codingbattle.common.exception.oauth.UserIdNotFoundException;
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
    private final UserRoleRepository userRoleRepository;
    private final LanguageRepository languageRepository;
    private final UserSettingRepository userSettingRepository;

    @Override
    @Transactional
    public ResponseDto signIn(SignInRequestDto signInRequestDto) {
        User user = userRepository.findByUserId(signInRequestDto.getUserId())
                .orElseThrow(() -> new UserIdNotFoundException(OauthErrorCode.USER_ID_NOT_FOUNT.getMessage()));

        validateSignInRequest(signInRequestDto, user);

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(user);

        saveRefreshToken(jwtTokenDto.getRefreshToken(), user.getId());

        return ResponseDto.builder()
                .status(OauthSuccessCode.SIGN_IN_SUCCESS.getStatus())
                .message(OauthSuccessCode.SIGN_IN_SUCCESS.getMessage())
                .data(jwtTokenDto)
                .build();
    }


    @Override
    @Transactional
    public ResponseDto singUp(SignUpRequestDto signUpRequestDto) {
        validateSignUpRequest(signUpRequestDto);

        User user = User.builder()
                .role(userRoleRepository.findByName(UserRoleType.ROLE_USER))
                .userId(signUpRequestDto.getUserId())
                .password(signUpRequestDto.getPassword())
                .build()
                .encodePassword(passwordEncoder);

        User savedUser = userRepository.save(user);
        Language language = languageRepository.findByName(signUpRequestDto.getLanguage().toLowerCase());

        userSettingRepository.save(UserSetting.builder()
                .user(savedUser)
                .language(language)
                .build());

        return ResponseDto.builder()
                .status(OauthSuccessCode.SIGN_UP_SUCCESS.getStatus())
                .message(OauthSuccessCode.SIGN_UP_SUCCESS.getMessage())
                .data(savedUser.getId())
                .build();
    }

    @Override
    @Transactional
    public ResponseDto refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        User user = userRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new CustomAuthenticationException(OauthErrorCode.USER_ID_NOT_FOUNT.getMessage()));

        Token token = tokenRepository.findByUserId(user.getId())
                .orElseThrow(
                        () -> new CustomAuthenticationException(OauthErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage()));

        // Refresh Token이 일치하지 않을 경우
        if (!token.getRefreshToken().equals(refreshToken.substring(7))) {
            throw new CustomAuthenticationException(OauthErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(user);

        saveRefreshToken(jwtTokenDto.getRefreshToken(), user.getId());

        return ResponseDto.builder()
                .status(OauthSuccessCode.REFRESH_TOKEN_SUCCESS.getStatus())
                .message(OauthSuccessCode.REFRESH_TOKEN_SUCCESS.getMessage())
                .data(jwtTokenDto)
                .build();
    }

    @Override
    @Transactional
    public ResponseDto checkUserId(String userId) {
        if (isExistUser(userId)) {
            throw new AlreadyExistUserIdException(OauthErrorCode.ALREADY_EXIST_USER_ID.getMessage());
        }

        return ResponseDto.builder()
                .status(OauthSuccessCode.CHECK_USER_ID_SUCCESS.getStatus())
                .message(OauthSuccessCode.CHECK_USER_ID_SUCCESS.getMessage())
                .build();
    }

    private void saveRefreshToken(String refreshToken, Long userId) {
        Optional<Token> jwtToken = tokenRepository.findByUserId(userId);

        if (jwtToken.isPresent()) {
            jwtToken.get().setRefreshToken(refreshToken);
        } else {
            tokenRepository.save(Token.builder()
                    .userId(userId)
                    .refreshToken(refreshToken)
                    .build());
        }
    }

    private void validateSignInRequest(SignInRequestDto signInRequestDto, User user) {
        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException(OauthErrorCode.PASSWORD_NOT_MATCH.getMessage());
        }
    }

    private void validateSignUpRequest(SignUpRequestDto signUpRequestDto) {
        if (isExistUser(signUpRequestDto.getUserId())) {
            throw new AlreadyExistUserIdException(OauthErrorCode.ALREADY_EXIST_USER_ID.getMessage());
        }

        if (!passwordCheck(signUpRequestDto.getPassword(), signUpRequestDto.getPasswordCheck())) {
            throw new PasswordCheckException(OauthErrorCode.PASSWORD_CHECK.getMessage());
        }

        if (ProgrammingLanguage.isNotContains(signUpRequestDto.getLanguage())) {
            throw new CustomAuthenticationException(OauthErrorCode.LANGUAGE_NOT_FOUND.getMessage());
        }
    }

    private boolean isExistUser(String userId) {
        return userRepository.existsByUserId(userId);
    }

    private boolean passwordCheck(String password, String passwordCheck) {
        return password.equals(passwordCheck);
    }
}
