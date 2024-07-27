package com.gongkademy.domain.member.controller;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.domain.member.service.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.gongkademy.domain.member.entity.MemberRole.ADMIN;
import static com.gongkademy.domain.member.entity.MemberRole.USER;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberServiceImpl memberService;

//    @PostMapping
//    public ResponseEntity<?> signup(@RequestBody MemberSignUpDTO memberSignUpDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        long loginMemberId = principalDetails.getMemberId();
//        memberService.joinMember(loginMemberId, memberSignUpDTO);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }

    @GetMapping("/role/{memberRole}")
    public ResponseEntity<?> checkMemberRole(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable String memberRole) {
        Long memberId = principalDetails.getMemberId();
        memberService.validateAuthority(memberId, MemberRole.valueOf(memberRole));

        return ResponseEntity.status(HttpStatus.OK).body("권한 확인에 성공했습니다.");
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
        memberService.modifyMember(loginMemberId, memberUpdateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원수정 성공");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        long loginMemberId = principalDetails.getMemberId();
        log.info("loginMemberId는 : " + loginMemberId);
        memberService.deleteMember(loginMemberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원탈퇴 성공");
    }

    // TODO: URL 경로에 대해 회의 필요
    @PatchMapping("/notification")
    public ResponseEntity<?> changeNotificationEnabledStatus(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        memberService.changeNotificationEnabledStatus(principalDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body("알림 on/off 상태 변경 성공");
    }
}
