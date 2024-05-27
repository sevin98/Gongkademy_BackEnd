package com.gongkademy.global.config;


import com.gongkademy.global.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {
    private final StringRedisTemplate redisTemplate;
    
    @Bean
    public RedisUtil redisUtil(){
        return new RedisUtil(redisTemplate);
    }

    @Value("${EMAIL_SENDER_USERNAME}")
    private String EMAIL_SENDER_USERNAME;

    @Value("${EMAIL_SENDER_PASSWORD}")
    private String EMAIL_SENDER_PASSWORD;
    @Value("${EMAIL_SENDER_PORT}")
    private int EMAIL_SENDER_PORT;
    @Value("${EMAIL_SENDER_HOST}")
    private String EMAIL_SENDER_HOST;
    @Value("${EMAIL_SENDER_SMTP_AUTH}")
    private String EMAIL_SENDER_SMTP_AUTH;


    @Value("${EMAIL_SENDER_SMTP_TIMEOUT}")
    private String EMAIL_SENDER_SMTP_TIMEOUT;
    @Value("${EMAIL_SENDER_SMTP_TTLS}")
    private String EMAIL_SENDER_SMTP_TTLS;



    @Bean
    public JavaMailSender mailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(EMAIL_SENDER_HOST);
        mailSender.setPort(EMAIL_SENDER_PORT);
        mailSender.setUsername(EMAIL_SENDER_USERNAME);
        mailSender.setPassword(EMAIL_SENDER_PASSWORD);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.transport.protocol", "smtp");//프로토콜로 smtp 사용
        javaMailProperties.put("mail.smtp.auth", EMAIL_SENDER_SMTP_AUTH);//smtp 서버에 인증이 필요
        javaMailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");//SSL 소켓 팩토리 클래스 사용
        javaMailProperties.put("mail.smtp.starttls.enable", "true");//STARTTLS(TLS를 시작하는 명령)를 사용하여 암호화된 통신을 활성화
        javaMailProperties.put("mail.debug", "true");//디버깅 정보 출력
        javaMailProperties.put("mail.smtp.ssl.trust", "smtp.naver.com");//smtp 서버의 ssl 인증서를 신뢰
        javaMailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");//사용할 ssl 프로토콜 버젼

        mailSender.setJavaMailProperties(javaMailProperties);//mailSender에 우리가 만든 properties 넣고
        return mailSender;
    }

}
