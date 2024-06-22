package com.gongkademy.domain.member.repository;

import com.gongkademy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @EntityGraph(attributePaths = {"memberRoleList"})
    Optional<Member> findByEmail(String email);

    @Override
    @EntityGraph(attributePaths = {"memberRoleList"})
    Optional<Member> findById(Long id);
}
