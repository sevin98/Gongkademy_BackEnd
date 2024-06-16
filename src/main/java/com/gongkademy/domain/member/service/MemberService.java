package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface MemberService {
    MemberInfoDTO getMemberInfo(Long id);
    Member join(MemberSignUpDTO dto);
    void updateNickname(MemberUpdateDTO dto);
// 현재는 닉네임 중복 허용이므로 주석처리
//    void validateDuplicateNickname(String nickname);
    void validateDuplicateEmail(String email);


    /**
     * 회원 엔티티를 회원 정보 DTO로 변환.
     * @param member 회원 엔티티
     * @return 회원 정보 DTO
     */
    default MemberInfoDTO entityToMemberInfoDTO(Member member) {
        return MemberInfoDTO.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .birthday(member.getBirthday() != null ? member.getBirthday().toString() : null)
                .university(member.getUniversity())
                .major(member.getMajor())
                .minor(member.getMinor())
                .member_role(member.getMemberRoleList().toString())
                .build();
    }

    /**
     * 회원 가입 정보 DTO를 회원 엔티티로 변환.
     * @param dto 회원 가입 정보 DTO
     * @return 회원 엔티티
     */
    default Member memberSignUpDTOtoEntity(MemberSignUpDTO dto) {
        return Member.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
//                .password(dto.getPassword())
                .birthday(LocalDate.parse(dto.getBirthday(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                .university(dto.getUniversity())
                .major(dto.getMajor())
                .minor(dto.getMinor())
                .build();
    }

    /**
     * 회원 업데이트 정보 DTO를 회원 엔티티로 변환.
     * @param dto 회원 업데이트 정보 DTO
     * @return 회원 엔티티
     */
    default Member memberUpdateDTOtoEntity(MemberUpdateDTO dto) {
        return Member.builder()
                .nickname(dto.getNewNickname())
                .university(dto.getUniversity())
                .major(dto.getMajor())
                .minor(dto.getMinor())
                .build();
    }
}
