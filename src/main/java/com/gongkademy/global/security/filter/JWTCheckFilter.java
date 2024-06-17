package com.gongkademy.global.security.filter;

import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.redis.RedisUtil;
import com.gongkademy.global.security.util.JWTUtil;
import com.google.gson.Gson;
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
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

//나중에 loginUrl 커스텀하면 여기도 바꿔야함
//    private static final String NO_CHECK_URL = "/login";
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

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

        String authHeaderStr = request.getHeader("Authorization");
        if (authHeaderStr == null || !authHeaderStr.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);
            log.info(claims);

            Optional<Long> userId = Optional.ofNullable(((Number) claims.get("pk")).longValue());
            userId.ifPresent(id -> {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    memberRepository.findById(id).ifPresent(this::saveAuthentication);
                }
            });

            // Refresh Token 처리 로직 추가
            String refreshToken = request.getHeader("RefreshToken");
            if (refreshToken != null && JWTUtil.isRefreshTokenValid(refreshToken)) {
                checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            } else {
                checkAccessTokenAndAuthentication(request, response, filterChain);
            }
        } catch (Exception e) {
            log.error("JWT 에러갖미 ======================", e);
            log.error(e.getMessage());

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 인증 객체를 생성하고 SecurityContextHolder에 설정하는 메서드
     * @param member 인증할 회원 객체
     */
    private void saveAuthentication(Member member) {
        PrincipalDetails principalDetails = new PrincipalDetails(member, null);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principalDetails, null, authoritiesMapper.mapAuthorities(principalDetails.getAuthorities())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * refreshToken 검사
     * accessToken과 refreshToken 토큰 발급
     */
    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        if (JWTUtil.isTokenValid(refreshToken)) {
            Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
            Long memberId = Long.parseLong(claims.get("pk").toString());
            String redisRefreshToken = redisUtil.getData(String.valueOf(memberId));

            if (refreshToken.equals(redisRefreshToken)) {
                String reIssuedRefreshToken = JWTUtil.createRefreshToken(memberId);
                jwtUtil.updateRefreshToken(memberId, reIssuedRefreshToken); // static메소드로 바꾸지 않고 인스턴스 메소드 호출
            }
        }
    }


    /**
     * 재발급된 refreshToekn return
     */
    private String reIssueRefreshToken(Long memberId) {
        String reIssuedRefreshToken = JWTUtil.createRefreshToken(memberId);
        jwtUtil.updateRefreshToken(memberId, reIssuedRefreshToken);
        return reIssuedRefreshToken;
    }

    /**
     * accessToken 검사 / 인증
     */
    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");

        jwtUtil.extractToken(request)
                .filter(JWTUtil::isTokenValid)
                .ifPresent(accessToken -> {
                    Map<String, Object> claims = JWTUtil.validateToken(accessToken);
                    Long userId = ((Number) claims.get("pk")).longValue();
                    memberRepository.findById(userId).ifPresent(this::saveAuthentication);
                });

        filterChain.doFilter(request, response);
    }
}
