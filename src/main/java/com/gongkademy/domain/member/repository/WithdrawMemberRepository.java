package com.gongkademy.domain.member.repository;

import com.gongkademy.domain.member.entity.WithdrawMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawMemberRepository extends JpaRepository<WithdrawMember, Long> {
}
