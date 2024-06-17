package com.gongkademy.global.config;

import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.member.service.UserDetailsServiceImpl;
import com.gongkademy.global.redis.RedisUtil;
import com.gongkademy.global.security.filter.JWTCheckFilter;
import com.gongkademy.global.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    JWTUtil jwtUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        log.info("---------------security config--------------------");

        //아래에서 정의한 cors설정을 사용하겠다.
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        //세션은 만들지 않겠다. => 왜 안만들지?
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
        });

        //csrf설정
        http.csrf(AbstractHttpConfigurer::disable);

        //로그인 시도
        http.formLogin(config->{
            //member/login url로 id,password를 같이 요청하면 => loadByUsername메소드가 실행
            config.loginPage("/api/v1/members/login");
            //성공하면 괄호 안에 내용을 실행할거야
//            config.successHandler(new APILoginSuccessHandler());
            //실패하면
//            config.failureHandler(new APILoginFailureHandler());
        });

        //필터 추가
        //UsernamePasswordAuthent~Filter앞에서 JWTFilter를 실행시켜줘
        http.addFilterBefore(new JWTCheckFilter(memberRepository, jwtUtil, redisUtil), UsernamePasswordAuthenticationFilter.class);
//        http.addFilterBefore(new CorsFilter(), SecurityContextHolderFilter.class);

        return http.build();
    }

    //패스워드 인코더는 필수임
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    //CORS설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedMethods(Arrays.asList("HEAD","GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization","Cache-Control","Content-Type","Access-Control-Allow-Origin","credentials"));
        configuration.setAllowCredentials(true);
        log.info("corsconfiguration: "+configuration.getAllowedOrigins());
    //
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);

        return source;
    }

    //userdetailservice를 컨테이너에 올리기
    @Bean
    public UserDetailsService UserDetailsServiceImpl() {
        return new UserDetailsServiceImpl(memberRepository);
    }
}
