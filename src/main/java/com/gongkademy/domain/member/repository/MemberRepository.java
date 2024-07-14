package com.gongkademy.domain.member.repository;

import com.gongkademy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @EntityGraph(attributePaths = {"memberRoleList"})
    Optional<Member> findByEmail(String email);

    @Override
    @EntityGraph(attributePaths = {"memberRoleList"})
    Optional<Member> findById(Long id);

    @Query("SELECT m.isNotificationEnabled FROM Member m WHERE m.id = :memberId")
    Boolean findIsNotificationEnabledById(@Param("memberId") Long id);

    @Query("SELECT m FROM Member m WHERE m.email = :email ORDER BY m.createTime DESC LIMIT 1")
    Optional<Member> findRecentlyCreateMemberByEmail(@Param("email") String email);
}
