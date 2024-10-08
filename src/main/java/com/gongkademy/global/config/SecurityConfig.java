package com.gongkademy.global.config;

import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.member.service.OAuth2MemberService;
import com.gongkademy.global.redis.RedisUtil;
import com.gongkademy.global.security.filter.ExceptionHandlerFilter;
import com.gongkademy.global.security.filter.JWTCheckFilter;
import com.gongkademy.global.security.handler.OAuth2LoginFailureHandler;
import com.gongkademy.global.security.handler.OAuth2LoginSuccessHandler;
import com.gongkademy.global.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;


    private final JWTUtil jwtUtil;
    private final JWTCheckFilter jwtCheckFilter;

    private final OAuth2MemberService oAuth2MemberService;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private static final String FRONT_DEFAULT_URL = "http://localhost:3000";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        //TODO: @AuthenticationPrincipal 어노테이션을 사용하지 않는 메서드 중 접근권한이 필요한 것 찾아야함
                        .requestMatchers(new AntPathRequestMatcher("/community/comment", "POST")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/community/consulting", "POST")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/community/consulting/{articleId}", "PATCH")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/community/consulting/{articleId}", "DELETE")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/community/question", "POST")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/community/question/{articleId}", "PATCH")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/community/question/{articleId}", "DELETE")).authenticated()
                        .requestMatchers(new AntPathRequestMatcher("/course/download")).authenticated()
                        .requestMatchers("/admin/**").hasRole(MemberRole.ADMIN.name()) // admin으로 시작하는건 admin만 접근 가능

                        .anyRequest().permitAll())
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                        .logoutSuccessUrl(FRONT_DEFAULT_URL))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new ExceptionHandlerFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl(FRONT_DEFAULT_URL, true)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2MemberService))
                        .successHandler(new OAuth2LoginSuccessHandler(jwtUtil))
                        .failureHandler(new OAuth2LoginFailureHandler()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Origin", "credentials"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}