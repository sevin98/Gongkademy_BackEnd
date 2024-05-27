package com.gongkademy.domain.member.repository;

import com.gongkademy.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByEmail(String email);


//    void updateNickname(Long id, MemberUpdateDTO dto);
//
//    void updatePassword(Long id, MemberPasswordUpdateDTO dto);

    void deleteMember(Long id);
}
