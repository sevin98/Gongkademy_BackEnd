package com.gongkademy.global.security.filter;

import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.global.redis.RedisUtil;
import com.gongkademy.global.security.handler.OAuth2LoginSuccessHandler;
import com.gongkademy.global.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Log4j2
@RequiredArgsConstructor
@Setter
@Getter
@Component
public class JWTCheckFilter extends OncePerRequestFilter {

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Value("${jwt-filter.exclude-methods}")
    private List<String> excludeMethods;
    @Value("${jwt-filter.exclude-endpoints}")
    private List<String> excludeEndpoints;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        log.info("메소드와 엔드포인트");
        log.info(excludeMethods);
        log.info(excludeEndpoints);
        return excludeMethods.contains(request.getMethod()) && excludeEndpoints.contains(request.getRequestURI());
    }

    /**
     * JWT 토큰을 검증하고 인증을 처리하는 메서드
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtUtil.extractToken(request).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ACCESS_TOKEN));

        if (jwtUtil.isTokenValid(accessToken)) {

            if(jwtUtil.isExpired(accessToken)) {
                long memberId = jwtUtil.extractMemberId(accessToken).orElseThrow(() -> new CustomException(ErrorCode.JWT_NULL_MEMBER_ID));
                Optional<String> refreshToken = jwtUtil.getRefreshToken(memberId);

                if (refreshToken.isPresent()) {
                    if (jwtUtil.isTokenValid(refreshToken.get())) {
                        if (!jwtUtil.isExpired(refreshToken.get())) {
                            String newAccessToken = jwtUtil.createAccessToken(memberId);
                            oAuth2LoginSuccessHandler.addAccessTokenCookie(response, newAccessToken);
                            saveAuthentication(memberId);
                        } else throw new CustomException(ErrorCode.JWT_EXPIRED_REFRESH);
                    }
                } else throw new CustomException(ErrorCode.JWT_NULL_REFRESH);
            }
            else{
                long memberId = jwtUtil.extractMemberId(accessToken).orElseThrow(() -> new CustomException(ErrorCode.JWT_NULL_MEMBER_ID));
                saveAuthentication(memberId);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 인증 객체를 생성하고 SecurityContextHolder에 설정하는 메서드
     */
    private void saveAuthentication(Long memberId) {
        memberRepository.findById(memberId).ifPresent(member -> {
            PrincipalDetails principalDetails = new PrincipalDetails(member, null);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails, null, authoritiesMapper.mapAuthorities(principalDetails.getAuthorities())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });
    }
}