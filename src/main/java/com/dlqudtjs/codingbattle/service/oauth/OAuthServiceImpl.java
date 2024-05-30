package com.dlqudtjs.codingbattle.service.oauth;

import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.ALREADY_EXIST_USER_ID;
import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.LANGUAGE_NOT_FOUND;
import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.PASSWORD_CHECK;
import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.PASSWORD_NOT_MATCH;
import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.REFRESH_TOKEN_NOT_FOUND;
import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.USER_ID_NOT_FOUNT;

import com.dlqudtjs.codingbattle.common.constant.ProgrammingLanguage;
import com.dlqudtjs.codingbattle.common.constant.UserRoleType;
import com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode;
import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.dto.oauth.JwtTokenDto;
import com.dlqudtjs.codingbattle.dto.oauth.SignInRequestDto;
import com.dlqudtjs.codingbattle.dto.oauth.SignUpRequestDto;
import com.dlqudtjs.codingbattle.entity.oauth.Token;
import com.dlqudtjs.codingbattle.entity.user.Language;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.entity.user.UserSetting;
import com.dlqudtjs.codingbattle.repository.token.TokenRepository;
import com.dlqudtjs.codingbattle.repository.user.LanguageRepository;
import com.dlqudtjs.codingbattle.repository.user.UserRepository;
import com.dlqudtjs.codingbattle.repository.user.UserRoleRepository;
import com.dlqudtjs.codingbattle.repository.user.UserSettingRepository;
import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
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
                .orElseThrow(() -> new Custom4XXException(
                        USER_ID_NOT_FOUNT.getMessage(),
                        USER_ID_NOT_FOUNT.getStatus()));

        validateSignInRequest(signInRequestDto, user);

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(user);

        saveRefreshToken(jwtTokenDto.getRefreshToken(), user.getId());

        return ResponseDto.builder()
                .status(OauthConfigCode.SIGN_IN_SUCCESS.getStatus().value())
                .message(OauthConfigCode.SIGN_IN_SUCCESS.getMessage())
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
                .status(OauthConfigCode.SIGN_UP_SUCCESS.getStatus().value())
                .message(OauthConfigCode.SIGN_UP_SUCCESS.getMessage())
                .data(savedUser.getId())
                .build();
    }

    @Override
    @Transactional
    public ResponseDto refreshToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        User user = userRepository.findByUserId(authentication.getName())
                .orElseThrow(() -> new Custom4XXException(
                        USER_ID_NOT_FOUNT.getMessage(),
                        USER_ID_NOT_FOUNT.getStatus()));

        Token token = tokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new Custom4XXException(
                        REFRESH_TOKEN_NOT_FOUND.getMessage(),
                        REFRESH_TOKEN_NOT_FOUND.getStatus()));

        // Refresh Token이 일치하지 않을 경우
        if (!token.getRefreshToken().equals(refreshToken.substring(7))) {
            throw new Custom4XXException(REFRESH_TOKEN_NOT_FOUND.getMessage(), REFRESH_TOKEN_NOT_FOUND.getStatus());
        }

        JwtTokenDto jwtTokenDto = jwtTokenProvider.generateToken(user);

        saveRefreshToken(jwtTokenDto.getRefreshToken(), user.getId());

        return ResponseDto.builder()
                .status(OauthConfigCode.REFRESH_TOKEN_SUCCESS.getStatus().value())
                .message(OauthConfigCode.REFRESH_TOKEN_SUCCESS.getMessage())
                .data(jwtTokenDto)
                .build();
    }

    @Override
    @Transactional
    public ResponseDto checkUserId(String userId) {
        if (isExistUser(userId)) {
            throw new Custom4XXException(ALREADY_EXIST_USER_ID.getMessage(), ALREADY_EXIST_USER_ID.getStatus());
        }

        return ResponseDto.builder()
                .status(OauthConfigCode.CHECK_USER_ID_SUCCESS.getStatus().value())
                .message(OauthConfigCode.CHECK_USER_ID_SUCCESS.getMessage())
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
            throw new Custom4XXException(PASSWORD_NOT_MATCH.getMessage(), PASSWORD_NOT_MATCH.getStatus());
        }
    }

    private void validateSignUpRequest(SignUpRequestDto signUpRequestDto) {
        if (isExistUser(signUpRequestDto.getUserId())) {
            throw new Custom4XXException(ALREADY_EXIST_USER_ID.getMessage(), ALREADY_EXIST_USER_ID.getStatus());
        }

        if (!passwordCheck(signUpRequestDto.getPassword(), signUpRequestDto.getPasswordCheck())) {
            throw new Custom4XXException(PASSWORD_CHECK.getMessage(), PASSWORD_CHECK.getStatus());
        }

        if (ProgrammingLanguage.isNotContains(signUpRequestDto.getLanguage())) {
            throw new Custom4XXException(LANGUAGE_NOT_FOUND.getMessage(), LANGUAGE_NOT_FOUND.getStatus());
        }
    }

    private boolean isExistUser(String userId) {
        return userRepository.existsByUserId(userId);
    }

    private boolean passwordCheck(String password, String passwordCheck) {
        return password.equals(passwordCheck);
    }
}
