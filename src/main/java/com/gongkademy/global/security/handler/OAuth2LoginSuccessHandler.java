package com.gongkademy.global.security.handler;

import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.global.security.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final String SIGNUP_URL = "/members";
    private static final String MAIN_URL = "/";
    private final JWTUtil jwtUtil;

    /**
     * 로그인 성공 후 핸들러 메서드
     * if 멤버의 role에 USER 가 없다면 (아직 GUEST만 존재) 회원가입 페이지로 이동
     * else 로그인
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails oAuthUser = (PrincipalDetails) authentication.getPrincipal();
        if (!oAuthUser.getRoleNames().contains(MemberRole.USER.getKey())) {
            String accessToken = jwtUtil.createAccessToken(oAuthUser.getMemberId());
            jwtUtil.sendAccessToken(response, accessToken);
            response.sendRedirect(SIGNUP_URL);
        } else {
            loginSuccess(response, oAuthUser);
            response.sendRedirect(MAIN_URL);
        }
    }

    private void loginSuccess(HttpServletResponse response, PrincipalDetails oAuthUser) {
        String accessToken = jwtUtil.createAccessToken(oAuthUser.getMemberId());
        String refreshToken = jwtUtil.createRefreshToken(oAuthUser.getMemberId());
        jwtUtil.setRefreshToken(oAuthUser.getMemberId(), refreshToken);
        jwtUtil.sendAccessToken(response, accessToken);
    }
}
