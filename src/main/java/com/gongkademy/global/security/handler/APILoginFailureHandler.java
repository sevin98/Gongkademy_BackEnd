package com.gongkademy.global.security.handler;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
public class APILoginFailureHandler implements AuthenticationFailureHandler {
    //API서버는 실패해도 실패했다고 꼭 전달을 해줘야해.
    //대부분 사이트는 로그인 실패시 일단 200을 던지고, 잘못됐다고 알려줌.
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("Login fail......"+exception);

        Gson gson = new Gson();
        //에러면 그냥 에러메세지를 던져라 => 상태코드가 없으면 200임.
        String jsonStr = gson.toJson(Map.of("error", "ERROR_LOGIN"));
        response.setContentType("application/json");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
