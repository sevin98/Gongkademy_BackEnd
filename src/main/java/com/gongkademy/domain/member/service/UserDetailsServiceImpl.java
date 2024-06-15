package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    //username = 로그인시 쓰는 아이디, UserDetails는 PrincipalDetails
    //그니까 아이디를 입력해서 PrincipalDetails를 반환해주는 메소드네
    //로그인 시도시 아래 메소드가 실행
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("---------------------loadUserByUsername------------------");
        log.info("username: " + username);

        //username을 받아서 메소드를 실행
        //일단 entity를 꺼내
        Optional<Member> member = memberRepository.findByEmail(username);

        if (member.isEmpty()) throw new UsernameNotFoundException("존재하지 않는 이메일입니다.");

        //로그인 성공시 PrincipalDetails를 쏜다.
        PrincipalDetails principalDetails = new PrincipalDetails(
                member.get(),
                null // 이 부분은 attributes가 필요 없다면 null로 설정
        );

        log.info("principalDetails:" + principalDetails);
        return principalDetails;
    }
}
