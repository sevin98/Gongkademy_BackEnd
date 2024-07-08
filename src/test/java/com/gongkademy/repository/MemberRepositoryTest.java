package com.gongkademy.repository;

import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.member.entity.MemberRole;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member setMember() {
        Member member = Member.builder()
                .email("test@gmail.com")
                .name("tnwls")
                .build();
        member.addRole(MemberRole.USER);
        return member;
    }

    @Test
    @DisplayName("회원 이메일로 조회 테스트")
    void findByEmail() {
        // Given
        Member member = setMember();
        memberRepository.save(member);

        // When
        Optional<Member> foundMember = memberRepository.findByEmail(member.getEmail());

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getEmail()).isEqualTo(member.getEmail());

    }

    @Test
    @DisplayName("회원 ID로 조회 테스트")
    void findById() {
        // Given
        Member member = setMember();
        memberRepository.save(member);

        // When
        Optional<Member> foundMember = memberRepository.findById(member.getId());

        // Then
        assertThat(foundMember).isPresent();
        assertThat(foundMember.get().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("알림 설정 여부 조회 테스트")
    void findIsNotificationEnabledById() {
        // Given
        Member member = setMember();
        memberRepository.save(member);

        // When
        boolean isNotificationEnabled = memberRepository.findIsNotificationEnabledById(member.getId());

        // Then
        assertThat(isNotificationEnabled).isTrue();
    }
}