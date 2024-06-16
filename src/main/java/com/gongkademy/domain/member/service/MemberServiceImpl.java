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
    public MemberInfoDTO getMemberInfo(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        //TODO: 회원 못찾으면 예외처리
        return entityToMemberInfoDTO(member);
    }

    /**
     * 새로운 회원을 가입시킵니다.
     * @param dto 회원 가입 정보 DTO
     * @return 저장된 회원 엔티티
     */
    @Override
    public Member join(MemberSignUpDTO dto) {
        Member member = memberSignUpDTOtoEntity(dto);
        member.addRole(MemberRole.USER);
        return memberRepository.save(member);
    }

    /**
     * 중복된 닉네임이 있는지 검증합니다.
     * @param nickname 닉네임
     * 현재는 닉네임 중복 허용이므로 주석처리
     */
//    @Override
//    public void validateDuplicateNickname(String nickname) {
//        memberRepository.findByNickname(nickname).ifPresent(m -> {
//            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
//        });
//    }

    /**
     * 중복된 이메일이 있는지 검증합니다.
     * @param email 이메일
     */
    @Override
    public void validateDuplicateEmail(String email) {
        memberRepository.findByEmail(email).ifPresent(m -> {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        });
    }

    /**.
     * @param dto 회원 업데이트 정보 DTO
     */
    @Override
    public void updateNickname(MemberUpdateDTO dto) {
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(IllegalArgumentException::new);
//        member.updateNickname(dto.getNewNickname());
    }

    /**
     * @param id 회원 ID
     */
//    @Override
//    public void deleteMember(Long id) {
//        memberRepository.deleteMember(id);
//    }


}
