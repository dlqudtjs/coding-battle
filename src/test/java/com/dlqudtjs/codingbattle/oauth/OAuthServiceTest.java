package com.dlqudtjs.codingbattle.oauth;

import com.dlqudtjs.codingbattle.common.dto.ResponseDto;
import com.dlqudtjs.codingbattle.dto.oauth.SignUpRequestDto;
import com.dlqudtjs.codingbattle.entity.user.User;
import com.dlqudtjs.codingbattle.common.constant.UserRoleType;
import com.dlqudtjs.codingbattle.entity.user.UserRole;
import com.dlqudtjs.codingbattle.repository.user.UserRepository;
import com.dlqudtjs.codingbattle.service.oauth.OAuthServiceImpl;
import com.dlqudtjs.codingbattle.common.constant.code.OauthSuccessCode;
import com.dlqudtjs.codingbattle.common.exception.oauth.AlreadyExistUserIdException;
import com.dlqudtjs.codingbattle.common.exception.oauth.OauthErrorCode;
import com.dlqudtjs.codingbattle.common.exception.oauth.PasswordCheckException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class OAuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private OAuthServiceImpl oAuthService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUpSuccessTest() {
        // given
        String userId = "testId";
        String password = "testPassword";
        String passwordCheck = "testPassword";
        String language = "java";
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(userId, password, passwordCheck, language);

        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(password);
        Mockito.when(userRepository.existsByUserId(Mockito.anyString())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(User.builder()
                .id(1L)
                .userId(userId)
                .password(password)
                .role(UserRole.builder().name(UserRoleType.ROLE_USER).build())
                .build());

        // when
        ResponseDto responseDto = oAuthService.singUp(signUpRequestDto);

        // then
        Assertions.assertThat(responseDto.getStatus()).isEqualTo(200);
        Assertions.assertThat(responseDto.getMessage()).isEqualTo(OauthSuccessCode.SIGN_UP_SUCCESS.getMessage());
        Assertions.assertThat(responseDto.getData()).isEqualTo(1L);
    }

    @Test
    @DisplayName("User Id 중복 에러 반환 테스트")
    void duplicateUserIdErrorTest() {
        // given
        String userId = "testId";
        String password = "testPassword";
        String passwordCheck = "testPassword";
        String language = "java";
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(userId, password, passwordCheck, language);

        Mockito.when(userRepository.existsByUserId(Mockito.anyString())).thenReturn(true);

        // when & then
        Assertions.assertThatThrownBy(() -> oAuthService.singUp(signUpRequestDto))
                .isInstanceOf(AlreadyExistUserIdException.class)
                .hasMessage(OauthErrorCode.ALREADY_EXIST_USER_ID.getMessage());
    }

    @Test
    @DisplayName("password check 불일치 에러 반환 테스트")
    void passwordCheckErrorTest() {
        // given
        String userId = "testId";
        String password = "testPassword";
        String passwordCheck = "testPasswordCheck";
        String language = "java";
        SignUpRequestDto signUpRequestDto = new SignUpRequestDto(userId, password, passwordCheck, language);

        // when & then
        Assertions.assertThatThrownBy(() -> oAuthService.singUp(signUpRequestDto))
                .isInstanceOf(PasswordCheckException.class)
                .hasMessage(OauthErrorCode.PASSWORD_CHECK.getMessage());
    }
}
