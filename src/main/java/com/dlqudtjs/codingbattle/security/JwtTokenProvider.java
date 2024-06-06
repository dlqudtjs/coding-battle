package com.dlqudtjs.codingbattle.security;

import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.EXPIRED_JWT;
import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.MALFORMED_JWT;
import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.SIGNATURE;
import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.TOKEN_NOT_FOUND;
import static com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode.UNSUPPORTED_JWT;

import com.dlqudtjs.codingbattle.common.constant.Header;
import com.dlqudtjs.codingbattle.common.constant.code.OauthConfigCode;
import com.dlqudtjs.codingbattle.common.exception.Custom4XXException;
import com.dlqudtjs.codingbattle.common.exception.UnknownException;
import com.dlqudtjs.codingbattle.dto.oauth.JwtToken;
import com.dlqudtjs.codingbattle.entity.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private Key key;

    @Value("${jwt.secret-key}")
    String secretKey;
    @Value("${jwt.access-token-validity-in-seconds}")
    private Long accessTokenValidityInMilliseconds;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidityInMilliseconds;
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public JwtToken generateToken(User user) {
        // 권한 가져오기 (ROLE_USER, ROLE_ADMIN 등)
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiration = new Date(now + accessTokenValidityInMilliseconds);
        String accessToken = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("authorities", authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        Date refreshTokenExpiration = new Date(now + refreshTokenValidityInMilliseconds);
        String refreshToken = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(refreshTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(Header.AUTHORIZATION.getHeaderName());
    }

    public String getUserName(String token) {
        return parseClaims(token).getSubject();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String token) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public void validateToken(String token) {
        try {
            if (!token.startsWith("Bearer ")) {
                throw new UnsupportedJwtException(UNSUPPORTED_JWT.getMessage());
            }

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.substring(7));

        } catch (NullPointerException e) {
            log.info("JWT token is null");
            throw new Custom4XXException(TOKEN_NOT_FOUND.getMessage(), TOKEN_NOT_FOUND.getStatus());
        } catch (SignatureException e) {
            log.info("Invalid JWT signature");
            throw new Custom4XXException(SIGNATURE.getMessage(), SIGNATURE.getStatus());
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT token");
            throw new Custom4XXException(MALFORMED_JWT.getMessage(), MALFORMED_JWT.getStatus());
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token");
            throw new Custom4XXException(EXPIRED_JWT.getMessage(), EXPIRED_JWT.getStatus());
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token");
            throw new Custom4XXException(UNSUPPORTED_JWT.getMessage(), UNSUPPORTED_JWT.getStatus());
        } catch (Exception e) {
            log.info("Unknown JWT token");
            throw new UnknownException(OauthConfigCode.UNKNOWN.getMessage());
        }
    }

    private Claims parseClaims(String accessToken) {
        validateToken(accessToken);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken.substring(7))
                .getBody();
    }
}


