package com.gongkademy.domain.member.controller;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.domain.member.service.MemberService;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("api/v1/members")
@RequiredArgsConstructor
@Log4j2
public class MemberController {
    private final MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        memberService.validateDuplicateNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("nickname",nickname));
    }

    @GetMapping("check-email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email){
        memberService.validateDuplicateEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("email",email));
    }


    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfoDTO> getMemberInfo(@PathVariable(name="memberId") long memberId) {
        MemberInfoDTO dto = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(dto);
    }
    @PostMapping("")
    public HttpStatus save(@RequestBody MemberSignUpDTO dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        memberService.join(dto);
        return HttpStatus.OK;
    }
    @DeleteMapping("/{memberId}")
    public HttpStatus deleteMember(@PathVariable(name="memberId") long memberId){
        memberService.deleteMember(memberId);
        return HttpStatus.OK;
    }


    @PatchMapping("update-member")
    public HttpStatus updateMember(@RequestBody MemberUpdateDTO dto){
        memberService.updateNickname(dto);
        return HttpStatus.OK;
    }

    //acesstoken의 헤더를 받아서 만료됐는지 보고
    //리프레시 토큰은 만료안됐는지를 봐라
    @PostMapping("refresh")
    public Map<String, Object> refresh(
            @RequestHeader("Authorization") String authHeader,
            String refreshToken
    ) {
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.JWT_NULL_REFRESH);
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomException(ErrorCode.JWT_HEADER_STRING);
        }

        String accessToken = authHeader.substring(7);

        //accessToken 만료여부
        if(!checkExpireToken(accessToken)){
            //만료가 안됐다면 들고 있던 토큰들 가지고 나가
            return Map.of("accessToken",accessToken,"refreshToken",refreshToken);
        }

        //refreshToken 검증
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        log.info("refresh.......claims"+claims);
        String newAccessToken = JWTUtil.generateToken(claims,10);
        //refreshToken 얼마 안남았으면 새로 발급
        String newRefreshToken = checkTime((Integer)claims.get("exp"))? JWTUtil.generateToken(claims,60*24):refreshToken;
        return Map.of("accessToken",newAccessToken,"refreshToken",newRefreshToken);




    }

    private boolean checkTime(Integer exp) {
        //JWT exp를 날짜로 변환
        Date expDate = new Date((long) exp * 1000);
        //현재 시간과의 차이 계산
        long gap = expDate.getTime() - System.currentTimeMillis();
        //분 단위 계산
        long leftMin = gap / (1000 * 60);
        //1시간도 안남았는지..
        return leftMin<60;
    }

    private boolean checkExpireToken(String token){
        try{
            JWTUtil.validateToken(token);
        }catch(CustomException ex){
            if(ex.getMessage().equals(ErrorCode.JWT_EXPIRED.getMessage())){
                return true;
            }
        }
        return false;
    }

}




