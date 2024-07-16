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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        Optional<Member> existingMember = memberRepository.findRecentlyCreateMemberByEmail(email);
        //UserDB에 Member가 있어
        if (existingMember.isPresent()) {
            log.info(existingMember.get().getClass());
            log.info(existingMember.get().getMemberRoleList().get(0).getClass());
            log.info("existing member가 있다.");
            //탈퇴여부 검사
            if (existingMember.get().isDeleted()) { //탈퇴된사람이라면
                log.info("getMember() : 같은 이메일의 탈퇴된 회원이 존재");
                LocalDateTime withdrawDate = existingMember.get().getDeletedTime();
                LocalDateTime currentTime = LocalDateTime.now();
                long monthsBetween = ChronoUnit.MONTHS.between(withdrawDate, currentTime);

                //탈퇴시간 검사
                if (monthsBetween < 1) {
                    log.info("현재 회원의 monthsBetween값 : " + monthsBetween);
                    throw new IllegalStateException("탈퇴 후 1달이 지나야 재가입이 가능합니다.");
                } else {
                    log.info("getMember() : 같은 이메일의 탈퇴했지만 한달 지난 회원 존재");
                    //한 달 지났다면 재가입
                    member = joinMember(email, name);
                }
            }else{
                log.info("getMember() : 같은 이메일의 탈퇴 안된 사람 중 같은 이메일 존재 -> 로그인으로 넘어감");
                //탈퇴 안된 사람이라면 정보 업데이트 (로그인)
                member = existingMember.get();
                member.updateName(name);
            }
        }else{
            log.info("getMember() : 같은 이메일의 회원 존재하지 않음");
            //기존DB에 없으면 join
            member = joinMember(email, name);
        }
        log.info("join할 member: " + member);
        log.info("join할 member의 Role: " + member.getMemberRoleList());
        Member save = memberRepository.save(member);

        return save;
    }

    private Member joinMember(String email, String name){
        Member member = Member.builder()
                .email(email)
                .name(name)
                .nickname("")
                .birthday(null)
                .agreeMarketing(false)
                .isNotificationEnabled(true)
                .build();
        member.addRole(MemberRole.GUEST); // 이후 회원가입을 위해 최초 로그인은 GUEST로 설정
        return member;
    }
}
