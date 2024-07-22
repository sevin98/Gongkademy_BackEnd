package com.gongkademy.global.security.handler;

import com.gongkademy.global.exception.CustomException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String errorMessage = "소셜 로그인 실패 ! 서버 로그를 확인해주세요.";
        if (exception instanceof OAuth2AuthenticationException oAuth2Exception) {
            errorMessage = oAuth2Exception.getError().getDescription();

            if (oAuth2Exception.getCause() instanceof CustomException customException) {
                errorMessage = customException.getErrorCode().getMessage();
                httpStatus = customException.getErrorCode().getHttpStatus();
            }
        }

        response.setStatus(httpStatus.value());
        response.getWriter().write(errorMessage);
        log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", errorMessage);
    }
}
