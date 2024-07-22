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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.PrintWriter;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String SIGNUP_URL = "http://localhost:3000/signup";
    private static final String MAIN_URL = "http://localhost:3000";

    private final JWTUtil jwtUtil;

    private String redirectUrl = "http://localhost:3000/auth/google/val";

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

        String mode = oAuthUser.getRoleNames().contains(MemberRole.USER.toString()) ? "login" : "signup";

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("mode",mode )
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // HTTPS를 사용하는 경우
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간 동안 유효
        response.addCookie(accessTokenCookie);
    }

    private void loginSuccess(HttpServletResponse response, PrincipalDetails oAuthUser) {
        String refreshToken = jwtUtil.createRefreshToken(oAuthUser.getMemberId());
        log.info("==loginSucess== refresh token : " + refreshToken);
        jwtUtil.setRefreshToken(oAuthUser.getMemberId(), refreshToken);
    }
}
