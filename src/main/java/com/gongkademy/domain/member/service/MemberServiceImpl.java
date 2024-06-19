package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    /**
     * 주어진 회원 ID로 회원 정보를 가져옵니다.
     * @param id 회원 ID
     * @return 회원 정보 DTO
     */
    @Override
    public MemberInfoDTO getMemberInfo(long id) {
        Member member = memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        //TODO: 회원 못찾으면 예외처리
        return entityToMemberInfoDTO(member);
    }

    /**
     * @param id 회원 ID
     * @param memberSignUpDTO 회원가입 정보
     * @return 회원 ID
     */
    @Override
    public Long joinMember(long id, MemberSignUpDTO memberSignUpDTO) {
        Optional<Member> optMember = memberRepository.findById(id);

        if (optMember.isEmpty()) return null;

        Member member = optMember.get();
        member.addRole(MemberRole.USER);
        member.signup(memberSignUpDTO);

        return member.getId();
    }

    /**
     * @param id 회원 ID
     * @param memberUpdateDTO 회원수정 정보
     * @return 회원 ID
     */
    @Override
    public Long modifyMember(long id, MemberUpdateDTO memberUpdateDTO) {
        Optional<Member> optMember = memberRepository.findById(id);

        if (optMember.isEmpty()) return null;

        Member member = optMember.get();
        member.addRole(MemberRole.USER);
        member.update(memberUpdateDTO);

        return member.getId();
    }

    /**
     * @param id 회원 ID
     */
    @Override
    public void deleteMember(long id) { memberRepository.deleteById(id); }
}
