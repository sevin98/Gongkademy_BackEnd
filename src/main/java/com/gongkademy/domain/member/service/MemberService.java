package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.dto.MemberSignUpDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface MemberService {
    MemberInfoDTO getMemberInfo(long id);
    Long joinMember(long id, MemberSignUpDTO dto);
    Long modifyMember(long id, MemberUpdateDTO dto);
    void deleteMember(long id);
    Long changeNotificationEnabledStatus(long id);

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
                .agreeMarketing(member.getAgreeMarketing())
                .member_role(member.getMemberRoleList().toString())
                .createTime(member.getCreateTime())
                .isNotificationEnabled(member.isNotificationEnabled())
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
                .agreeMarketing(dto.getAgreeMarketing())
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
                .agreeMarketing(dto.getAgreeMarketing())
                .build();
    }
}
