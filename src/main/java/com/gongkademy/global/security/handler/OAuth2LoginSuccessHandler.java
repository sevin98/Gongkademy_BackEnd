package com.gongkademy.global.security.handler;

import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.global.security.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private String redirectUrl = "http://localhost:3000/auth/google/val";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails oAuthUser = (PrincipalDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.createAccessToken(oAuthUser.getMemberId());
        addAccessTokenCookie(response, accessToken);

        String refreshToken = jwtUtil.createRefreshToken(oAuthUser.getMemberId());
        jwtUtil.setRefreshToken(oAuthUser.getMemberId(), refreshToken);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl).build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간 동안 유효
        response.addCookie(accessTokenCookie);
    }
}
