package com.gongkademy.domain.member.controller;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberServiceImpl memberService;

    @PostMapping
    public ResponseEntity<?> signup(@RequestBody MemberSignUpDTO memberSignUpDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        long loginMemberId = principalDetails.getMemberId();
        if (memberService.joinMember(loginMemberId, memberSignUpDTO) == null) {
            log.info("회원가입 후 권한 :" + principalDetails.getRoleNames());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<MemberInfoDTO> getMemberInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        long loginMemberId = principalDetails.getMemberId();
        MemberInfoDTO memberInfo = memberService.getMemberInfo(loginMemberId);
        log.info("조회 시 권한 :" + principalDetails.getRoleNames());
        return ResponseEntity.ok(memberInfo);
    }

    @PatchMapping
    public ResponseEntity<?> updateMember(@RequestBody MemberUpdateDTO memberUpdateDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        long loginMemberId = principalDetails.getMemberId();
        if (memberService.modifyMember(loginMemberId, memberUpdateDTO) == null) {
            log.info("수정 후 권한 :" + principalDetails.getRoleNames());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else
            return ResponseEntity.status(HttpStatus.CREATED).body("회원수정 성공");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        long loginMemberId = principalDetails.getMemberId();
        log.info("loginMemberId는 : " + loginMemberId);
        Long memberId = memberService.deleteMember(loginMemberId);
        if (memberId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원탈퇴 실패: 회원을 찾을 수 없음");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원탈퇴 성공");
    }

    // TODO: URL 경로에 대해 회의 필요
    @PatchMapping("/notification")
    public ResponseEntity<?> changeNotificationEnabledStatus(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (memberService.changeNotificationEnabledStatus(principalDetails.getMemberId()) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("알림 on/off 상태 변경 실패");
        } else return ResponseEntity.status(HttpStatus.CREATED).body("알림 on/off 상태 변경 성공");
    }
}
