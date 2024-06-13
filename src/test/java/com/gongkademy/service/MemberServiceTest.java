package com.gongkademy.service;

import com.gongkademy.domain.member.dto.MemberInfoDTO;
import com.gongkademy.domain.member.dto.MemberUpdateDTO;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.member.service.MemberServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {
    @Autowired
    MemberServiceImpl service;
    @Autowired
    private PasswordEncoder passwordEncoder; //테스트시 비밀번호는 인코딩된 상태로 넣어야함.
    @Autowired
    MemberRepository repository;

    /*
    * TODO:
    *  email 인증메일,
    *  인증체크 기능.
    *  닉네임 중복 예외
    *  이메일 중복 예외
    * */

    @Test
    void 닉네임_수정(){
        Member member1 = setMember();
        MemberUpdateDTO dto = new MemberUpdateDTO(member1.getEmail(),"userB");
        service.updateNickname(dto);
        MemberInfoDTO infoDto = service.getMemberInfo(member1.getId());
        assertThat("userB").isEqualTo(infoDto.getNickname());
    }

    @Test
    void 회원삭제(){
        //TODO여기부터 짜기
        Member member1 = setMember();
        service.deleteMember(member1.getId());
        Optional<Member> member2 = repository.findById(member1.getId());
        assertThat(member2).isEmpty();
    }

    private Member setMember() {
        Member member = Member.builder()
                .email("user@naver.com")
                .password(passwordEncoder.encode("1234"))
                .nickname("USERA")
                .birthday(LocalDate.of(1998, 7, 18))
                .build();
        member.addRole(MemberRole.USER);
        repository.save(member);
        return member;
    }
}