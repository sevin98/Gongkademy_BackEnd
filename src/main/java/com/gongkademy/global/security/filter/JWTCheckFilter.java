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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


@Log4j2
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

//    private static final String NO_CHECK_URL = "/login";
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    /**
     * 특정 경로에 대해 필터링을 제외하기 위한 메서드
     * @return 필터링을 제외할 경우 true 반환
     * 개발단계에서는 해당 메소드 사용할 필요 없음
     * 나중에 loginUrl 커스텀하면 여기도 바꿔야함
     */
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//        String path = request.getRequestURI();
//        log.info("url검사 ======================" + path);
//        return path.equals(NO_CHECK_URL);
//    }

    /**
     * JWT 토큰을 검증하고 인증을 처리하는 메서드
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWTCheckFilter doFilterInternal 메소드 ===================");

        //accessToken 처리
        String accessToken = jwtUtil.extractToken(request).get();
        log.info("accessToken : " + accessToken);
        //access 토큰검증
        if (jwtUtil.isTokenValid(accessToken)) {
            log.info("accessToken 검증 완료, 유효함");
            //검증한 토큰이 만료
            if(jwtUtil.isExpired(accessToken)) {
                log.info("accessToken 검증 완료 2, 만료됨");
                long memberId = jwtUtil.extractMemberId(accessToken).get();
                // Refresh Token 처리 로직 추가
                //memberId로 refreshToken 가져옴
                Optional<String> refreshToken = jwtUtil.getRefreshToken(memberId);

                //refreshToken 유효성 검증 , 유효한 토큰이라면 accessToken 재발급
                if (refreshToken.isPresent()) {
                    log.info("refreshToken 존재, accessToken 재발급");
                    if (jwtUtil.isTokenValid(refreshToken.get())) {
                        String newAccessToken = jwtUtil.createAccessToken(memberId);
                        oAuth2LoginSuccessHandler.addAccessTokenCookie(response, newAccessToken);
                        log.info("newAccessToken 재발급 완료" + newAccessToken);
                        saveAuthentication(memberId);
                    }
                } else throw new CustomException(ErrorCode.JWT_NULL_REFRESH);
            }
            else{
                //만료되지 않았다면
                long memberId = jwtUtil.extractMemberId(accessToken).get();
                //securityContextHolder에 저장
                saveAuthentication(memberId);
            }
        }
        log.info("최종 dofilter 동작================");
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
            log.info("saveAuthentication 동작 =========================");
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });
    }
}
