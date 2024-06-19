package com.gongkademy.repository;

import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.domain.member.entity.MemberRole;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;
//    @Autowired
//    private PasswordEncoder passwordEncoder; //테스트시 비밀번호는 인코딩된 상태로 넣어야함.
//    //TODO: 회원찾기 => 조회결과가 없을때 테스트케이스 작성
//    @Test
//    void 닉네임으로_회원_찾기() {
//        Member member1 = setMember();
//        repository.save(member1);
//
//        Optional<Member> member2 = repository.findByNickname("USERA");
//        assertThat(member1).isEqualTo(member2.get());
//    }
//    @Test
//    void 이메일로_회원_찾기() {
//        Member member1 = setMember();
//        repository.save(member1);
//        Optional<Member> member2 = repository.findByEmail("user@naver.com");
//        assertThat(member1).isEqualTo(member2.get());
//        log.info("권한: "+member1.getMemberRoleList());
//    }
//
//    @Test
//    void 아이디로_회원_찾기(){
//        Member member1 = setMember();
//        repository.save(member1);
//        Optional<Member> member2 = repository.findById(member1.getId());
//        assertThat(member1).isEqualTo(member2.get());
//    }

//    @Test
//    void 닉네임_수정(){
//        Member member1 = setMember();
//        repository.save(member1);
//        MemberUpdateDTO dto = new MemberUpdateDTO("userB");
//        .updateNickname(member1.getId(),dto);
//        Member member2 = repository.findById(member1.getId()).get();
//        assertThat("userB").isEqualTo(member2.getNickname());
//    }

//    @Test
//    void 비밀번호_수정(){
//        Member member1 = setMember();
//        repository.save(member1);
//        String password = passwordEncoder.encode("123456");
//        MemberPasswordUpdateDTO dto = new MemberPasswordUpdateDTO(password);
//        repository.updatePassword(member1.getId(),dto);
//        Member member2 = repository.findById(member1.getId()).get();
//        assertThat(password).isEqualTo(member2.getPassword());
//    }

//    @Test
//    void 회원삭제(){
//        //TODO여기부터 짜기
//        Member member1 = setMember();
//        repository.save(member1);
//        repository.deleteMember(member1.getId());
//        Optional<Member> member2 = repository.findById(member1.getId());
//        assertThat(member2).isEmpty();
//    }
//
//    private Member setMember() {
//        Member member = Member.builder()
//                        .email("user@naver.com")
//                        .password(passwordEncoder.encode("manggom234**"))
//                                .nickname("USERA")
//                .birthday(LocalDate.of(1998, 7, 18))
//                .build();
//        member.addRole(MemberRole.USER);
//        return member;
//    }
}