package com.gongkademy.global.security.handler;

import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.global.security.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

    private static final String SIGNUP_URL = "http://localhost:3000/signup";
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
        String accessToken = jwtUtil.createAccessToken(oAuthUser.getMemberId());

        addAccessTokenCookie(response, accessToken);

        String redirectUrl = oAuthUser.getRoleNames().contains(MemberRole.USER.toString()) ? MAIN_URL : SIGNUP_URL;
        log.info("MemberRole: " + oAuthUser.getRoleNames());
        log.info("redirectUrl: " + redirectUrl);
        log.info("accessToken: " + accessToken);
        if(redirectUrl.equals(MAIN_URL)){
            loginSuccess(response, oAuthUser);
        }
        response.sendRedirect(redirectUrl);
    }

    private void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS를 사용하는 경우
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간 동안 유효
        response.addCookie(accessTokenCookie);
    }

    private void loginSuccess(HttpServletResponse response, PrincipalDetails oAuthUser) {
        String accessToken = jwtUtil.createAccessToken(oAuthUser.getMemberId());
        log.info("loginSucess: " + accessToken);
        String refreshToken = jwtUtil.createRefreshToken(oAuthUser.getMemberId());
        log.info("loginSucess: " + accessToken);
        jwtUtil.setRefreshToken(oAuthUser.getMemberId(), refreshToken);
    }
}
