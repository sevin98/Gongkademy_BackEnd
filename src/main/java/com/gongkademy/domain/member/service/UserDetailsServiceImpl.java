package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.dto.MemberDTO;
import com.gongkademy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    //username = 로그인시 쓰는 아이디, UserDetails는 MemberDTO
    //그니까 아이디를 입력해서 dto를 반환해주는 메소드네
    //로그인 시도시 아래 메소드가 실행
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("---------------------loadUserByUsername------------------");
        log.info("username: "+username);

        //username을 받아서 메소드를 실행
        //일단 entity를 꺼내
        Optional<Member> member = memberRepository.findByEmail(username);

        if(member.isEmpty()) throw new UsernameNotFoundException("존재하지 않는 이메일입니다.");

        //로그인 성공시 MemberDTO를 쏜다.
        MemberDTO memberDTO = new MemberDTO(
                member.get().getEmail(),
                member.get().getPassword(),
                member.get().getNickname(),
                member.get().getBirthday().toString(),
                member.get().getMemberRoleList()
                        .stream()
                        .map(Enum::name).collect(Collectors.toList())
        );

        log.info("memberDTO:"+memberDTO);
        return memberDTO;
    }
}
