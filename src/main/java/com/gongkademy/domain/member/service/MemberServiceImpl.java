package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.global.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final String DELETE_NICKNAME = "탈퇴회원";

    /**
     * 주어진 회원 ID로 회원 정보를 가져옵니다.
     * @param id 회원 ID
     * @return 회원 정보 DTO
     */
    @Override
    public MemberInfoDTO getMemberInfo(long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        return entityToMemberInfoDTO(member);
    }

    /**
     * @param id 회원 ID
     * @param memberSignUpDTO 회원가입 정보
     * @return 회원 ID
     */
    @Override
    public void joinMember(long id, MemberSignUpDTO memberSignUpDTO) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        member.addRole(MemberRole.USER);
        member.signup(memberSignUpDTO);

        String refreshToken = jwtUtil.createRefreshToken(member.getId());
        jwtUtil.setRefreshToken(member.getId(), refreshToken);
    }

    /**
     * @param id 회원 ID
     * @param memberUpdateDTO 회원수정 정보
     * @return 회원 ID
     */
    @Override
    public void modifyMember(long id, MemberUpdateDTO memberUpdateDTO) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        member.update(memberUpdateDTO);
    }

    /**
     * 실제 삭제가 아닌 soft-delete를 구현
     * @param id memberId
     * @return memberId
     */
    @Override
    public void deleteMember(long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));

        member.deleteMember(DELETE_NICKNAME + member.getId());
    }

    @Override
    public void changeNotificationEnabledStatus(long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.INVALID_MEMBER_ID));
        member.changeIsNotificationEnabled();
    }


}
