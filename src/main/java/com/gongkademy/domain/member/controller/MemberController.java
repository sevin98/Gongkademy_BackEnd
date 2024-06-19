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
        if (memberService.joinMember(loginMemberId, memberSignUpDTO) == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        else
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<MemberInfoDTO> getMemberInfo(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        long loginMemberId = principalDetails.getMemberId();
        MemberInfoDTO memberInfo = memberService.getMemberInfo(loginMemberId);
        return ResponseEntity.ok(memberInfo);
    }

    @PatchMapping
    public ResponseEntity<?> updateMember(@RequestBody MemberUpdateDTO memberUpdateDTO, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        long loginMemberId = principalDetails.getMemberId();
        if (memberService.modifyMember(loginMemberId, memberUpdateDTO) == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        else
            return ResponseEntity.status(HttpStatus.CREATED).body("회원수정 성공");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        long loginMemberId = principalDetails.getMemberId();
        memberService.deleteMember(loginMemberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("회원탈퇴 성공");
    }
}
