package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    /**
     * OAuth2UserRequest를 사용하여 사용자를 로드합니다.
     * @param userRequest OAuth2UserRequest
     * @return OAuth2User 객체
     * @throws OAuth2AuthenticationException 인증 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        Member member = getMember(email, name);

        // 기존 사용자 정보를 사용하여 OAuth2User 객체 반환
        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }

    private Member getMember(String email, String name) {
        Member member;
        Optional<Member> existingMember = memberRepository.findByEmail(email);
        // 멤버가 데이터베이스에 존재하지 않으면 새로운 멤버 생성
        if (existingMember.isEmpty()) {
            member = Member.builder()
                    .email(email)
                    .name(name)
                    .nickname("")
                    .birthday(null)
                    .university("")
                    .major("")
                    .minor("")
                    .build();
            member.addRole(MemberRole.GUEST); // 이후 회원가입을 위해 최초 로그인은 GUEST로 설정
        } else {
            member = existingMember.get();
            member.updateName(name); // 멤버 정보 업데이트 되었다면 멤버 이름 업데이트
        }
        return memberRepository.save(member);
    }
}
