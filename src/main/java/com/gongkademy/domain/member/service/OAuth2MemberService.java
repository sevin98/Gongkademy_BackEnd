package com.gongkademy.domain.member.service;

import com.gongkademy.domain.member.dto.PrincipalDetails;
import com.gongkademy.domain.member.entity.Member;
import com.gongkademy.domain.member.entity.MemberRole;
import com.gongkademy.domain.member.repository.MemberRepository;
import com.gongkademy.global.exception.CustomException;
import com.gongkademy.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
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
     *
     * @param userRequest OAuth2UserRequest
     * @return OAuth2User 객체
     * @throws OAuth2AuthenticationException 인증 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        try {
            Member member = getMember(email, name);
            return new PrincipalDetails(member, oAuth2User.getAttributes());
        } catch (CustomException e) {
            throw new OAuth2AuthenticationException(new OAuth2Error(e.getErrorCode().name(), e.getErrorCode().getMessage(), null), e.getErrorCode().getMessage(), e);
        }
    }

    private Member getMember(String email, String name) {
        Member member;
        Optional<Member> memberOptional = memberRepository.findFirstByEmailOrderByCreateTimeDesc(email);

        if (memberOptional.isPresent()) {
            member = memberOptional.get();

            if (member.isDeleted()) { // 탈퇴된 사람이라면
                LocalDateTime withdrawDate = member.getDeletedTime();
                LocalDateTime currentTime = LocalDateTime.now();
                long monthsBetween = ChronoUnit.MONTHS.between(withdrawDate, currentTime);

                if (monthsBetween < 1) {
                    throw new CustomException(ErrorCode.REJOIN_AFTER_ONE_MONTH);
                } else {
                    member = joinMember(email, name);
                }
            } else {
                member.updateName(name);
            }
        } else {
            member = joinMember(email, name);
        }
        return memberRepository.save(member);
    }

    private Member joinMember(String email, String name) {
        Member member = Member.builder()
                .email(email)
                .name(name)
                .nickname(name)
                .birthday(null)
                .agreeMarketing(false)
                .isNotificationEnabled(true)
                .build();
        member.addRole(MemberRole.USER);
        return member;
    }
}
