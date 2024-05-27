package com.gongkademy.domain.member.service;

import com.gongkademy.global.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class MailSendService {
    @Autowired
    private JavaMailSender mailSender;
    private int authNumber;
    @Value("${EMAIL_SENDER_USERNAME}")
    private String EMAIL_SENDER_USERNAME;

    @Autowired
    private RedisUtil redisUtil;

    public void makeRandomNumber(){
        Random r = new Random();
        String randomNumber = "";
        for(int i=0;i<6;i++){
            randomNumber += Integer.toString(r.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber);
    }

    public String joinEmail(String email){
        //TODO: 이미 이메일 인증메일을 보냈다면 그건 REDIS에서 지워야해.
        makeRandomNumber();
        String setFrom = EMAIL_SENDER_USERNAME;
        String toMail = email;
        String title = "회원 가입 인증 이메일 입니다."; //이메일 제목
        String content = "인증 번호는"+authNumber+"입니다.";
        mailSend(setFrom,toMail,title,content);
        return Integer.toString(authNumber);
    }

    public void mailSend(String setFrom, String toMail, String title, String content){
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"utf-8");
            //true를 전달하여 multipart 형식의 메세지를 지원하고, "utf-8"을 전달하여 문자 인코딩을 설정
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content,true);
            mailSender.send(message);
        }catch (MessagingException e){
            e.printStackTrace();
        }
        redisUtil.setDataExpire(Integer.toString(authNumber),toMail,60*5L);
    }

    public boolean CheckAuthNum(String email, String authNum){
        if(redisUtil.getData(authNum) == null){
            return false;
        } else if (redisUtil.getData(authNum).equals(email)) {
            return true;
        }else{
            return false;
        }
    }
}
