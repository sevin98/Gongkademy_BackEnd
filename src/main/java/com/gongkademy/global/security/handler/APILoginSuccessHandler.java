package com.gongkademy.global.security.handler;

import com.gongkademy.domain.member.dto.MemberDTO;
import com.gongkademy.global.security.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
//너는 bean으로 가지말고 config에서 설정
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    //로그인 성공시 뭐할건지? => jwt토큰 발급을 해야겠지! 그걸 json으로 던져!
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("----------인증정보----------");
        log.info(authentication);
        log.info("-----------------");

        //인증정보 authentication의 principal memberDTO임 그걸 jwt로 바꿔줘야해
        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();

        Map<String,Object> claims = memberDTO.getClaims();

        //사용자의 property를 모으는게 claim이니까 token도 여기다 모으는구나
        //담고싶은 정보를 claims에 만들어서 넣기, 10은 10분 유지하겠다.
        String accessToken = JWTUtil.generateToken(claims,10);
        String refreshToken = JWTUtil.generateToken(claims,10);

        claims.put("accessToken",accessToken);
        claims.put("refreshToken",refreshToken);

        //claim을 json으로 던져야하니까 변화
        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims);

        //응답에 정보담아
        response.setContentType("aplication/json; charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
