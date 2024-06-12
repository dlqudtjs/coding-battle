package com.dlqudtjs.codingbattle.configuration.security;

import com.dlqudtjs.codingbattle.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        log.info("request : " + request.getRequestURI());

        // header에 Authorization이 없거나 Bearer로 시작하지 않거나 토큰이 유효하지 않은 경우
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ") ||
                !jwtTokenProvider.isTokenValid(authorizationHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = jwtTokenProvider.getAuthentication(authorizationHeader);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().equals("/socket-endpoint");
    }
}
