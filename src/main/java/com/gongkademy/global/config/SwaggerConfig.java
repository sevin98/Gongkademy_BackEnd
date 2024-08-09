package com.gongkademy.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
요청값
성공코드
에러코드,메세지
도메인별 페이지 분리
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi group1() {
        String[] pathsToMatch = {"/community/**"};

        return GroupedOpenApi.builder()
                .group("커뮤니티 유저")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi group2() {
        String[] pathsToMatch = {"/members/**"};

        return GroupedOpenApi.builder()
                .group("멤버")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi group3() {
        String[] pathsToMatch = {"/course/**", "/review/**", "/player/**"};

        return GroupedOpenApi.builder()
                .group("강좌 유저")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi group4() {
        String[] pathsToMatch = {"/admin/community/**"};

        return GroupedOpenApi.builder()
                .group("커뮤니티 관리자")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public GroupedOpenApi group5() {
        String[] pathsToMatch = {"/admin/course/**", "/admin/lecture/**"};

        return GroupedOpenApi.builder()
                .group("강좌 관리자")
                .pathsToMatch(pathsToMatch)
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Gongkademy API"));
    }

}