package com.gongkademy.domain.member.repository;

import com.gongkademy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @EntityGraph(attributePaths = {"memberRoleList"})
    List<Member> findByEmail(String email);

    @Override
    @EntityGraph(attributePaths = {"memberRoleList"})
    Optional<Member> findById(Long id);

    @Query("SELECT m.isNotificationEnabled FROM Member m WHERE m.id = :memberId")
    Boolean findIsNotificationEnabledById(@Param("memberId") Long id);

    @EntityGraph(attributePaths = {"memberRoleList"})
    Optional<Member> findFirstByEmailOrderByCreateTimeDesc(String email);
}
