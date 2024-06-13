package com.gongkademy.domain.member.service;


import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.global.exception.ErrorCode;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public MemberInfoDTO getMemberInfo(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        //TODO: 회원 못찾으면 예외처리
        return entityToMemberInfoDTO(member);
    }

    @Override
    public Member join(MemberSignUpDTO dto) {
        Member member = memberSignUpDTOtoEntity(dto);
        member.addRole(MemberRole.USER);
        return memberRepository.save(member);
    }

    @Override
    public void validateDuplicateNickname(String nickname) {
        memberRepository.findByNickname(nickname).ifPresent(m -> {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        });
    }

    @Override
    public void validateDuplicateEmail(String email) {
        memberRepository.findByEmail(email).ifPresent(m -> {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        });
    }

    @Override
    public void updateNickname(MemberUpdateDTO dto) {
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(IllegalArgumentException::new);
        member.updateNickname(dto.getNewNickname());
    }

    @Override
    public void deleteMember(Long id) {
        memberRepository.deleteMember(id);
    }
}
