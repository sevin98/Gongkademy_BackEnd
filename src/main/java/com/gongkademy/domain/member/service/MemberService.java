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
    void deleteMember(Long id);
    void validateDuplicateNickname(String nickname);
    void validateDuplicateEmail(String email);
    default MemberInfoDTO entityToMemberInfoDTO(Member member){
        return MemberInfoDTO.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .birthday(member.getBirthday().toString())
                .build();
    }
    default Member memberSignUpDTOtoEntity(MemberSignUpDTO dto){
        return Member.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .birthday(LocalDate.parse(dto.getBirthday(), DateTimeFormatter.ofPattern("yyyyMMdd")))
                .build();
    }

    default Member memberUpdateDTOtoEntity(MemberUpdateDTO dto){
        return Member.builder()
                .nickname(dto.getNewNickname())
                .build();
    }


}
