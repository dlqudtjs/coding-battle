package com.dlqudtjs.codingbattle.security;

import com.dlqudtjs.codingbattle.common.constant.Header;
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
import io.jsonwebtoken.security.SecurityException;
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
    private String secretKey;
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
                .claim("userId", user.getId())
                .claim("authorities", authorities)
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        Date refreshTokenExpiration = new Date(now + refreshTokenValidityInMilliseconds);
        String refreshToken = Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId())
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

    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public String getUserName(String token) {
        return parseClaims(token).getSubject();
    }

    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String token) {
        // Jwt 토큰 복호화
        Long userId = getUserId(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.substring(7));
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature -> Message: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token -> Message:{}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token -> Message:{}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty -> Message:{}", e.getMessage());
        } catch (Exception e) {
            log.error("Invalid JWT token -> Message:{}", e.getMessage());
        }

        return false;
    }

    private Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken.substring(7))
                .getBody();
    }
}


