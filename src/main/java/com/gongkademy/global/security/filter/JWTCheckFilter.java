package com.gongkademy.global.security.filter;

import com.gongkademy.global.security.util.JWTUtil;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {
    //jwt토큰이 들어왔을때 검증하는 필터
    //OncePerRequestFilter 는 요청이 들어올때마다 필터가공하겠다.

    @Override
    //필터하지 않는 경로 설정 => 로그인과정에서는 필터하면 안됨. 개는 무조건 없잖아.
    //추가로 안하는 과정을 설정해줘야할듯
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        //true == not checking
        String path = request.getRequestURI();
        log.info("check uri---------" + path);

        //false == check

        //TODO: 필터적용안할애들 상수로 빼놓으면 됨 개발 중에는 필터없이
//        return path.equals("/api/v1/members");
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("--------------------");

        log.info("--------------------");

        log.info("--------------------");

        String authHeaderStr = request.getHeader("Authorization");

        //인증으로 온 헤더는 Beaer타입을 가짐
        //accessToken 파싱
        try {
            //성공
            String accessToken = authHeaderStr.substring(7); //"Bearer "는 잘라내야함. 그 뒤가 acessToken
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);
            log.info(claims);
        }catch(Exception e){
            //실패
            log.error("JWT Check Error................");
            log.error(e.getMessage());

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error","ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }

        //다음 필터로 이동하세요.
        filterChain.doFilter(request,response);
    }


}
