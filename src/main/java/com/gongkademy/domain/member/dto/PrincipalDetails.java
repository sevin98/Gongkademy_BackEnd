package com.gongkademy.domain.member.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.gongkademy.domain.member.entity.Member;

import java.time.LocalDate;
import java.util.*;

public class PrincipalDetails implements OAuth2User, UserDetails {

    private Member member;
    private Map<String, Object> attributes;

    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    //ROLE 기반 GrantedAuthority 목록 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + member.getMemberRoleList().toString()));
    }

    //Oauth2인증 시 제공된 맵 반환
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //Email 반환
    public String getEmail() {
        return member.getEmail();
    }

    //nickname 반환
    public String getNickname() {
        return member.getNickname();
    }

    //password 반환
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        //email로관리
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //birthday 반환
    public LocalDate getBirthday() {
        return member.getBirthday();
    }

    //RoleList안에있는 Role 반환
    public List<String> getRoleNames() {
        return Collections.singletonList(member.getMemberRoleList().toString());
    }

    public String getUniversity(){
        return member.getUniversity();
    }

    public String getMajor(){
        return member.getMajor();
    }

    public String getMinor(){
        return member.getMinor();
    }

    // JWT 클레임 : 이메일과 닉네임이 포함되어있는 claim반환
    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", getEmail());
        claims.put("nickname", getNickname());
        return claims;
    }

    // OAuth2User 인터페이스는 getName() 메서드를 포함하고있기떄문에 OAuth2User의 모든 메소드 반환차 필요
    // 우리는 email을 식별자로 사용하고싶기때문에 email 반환
    // or
    // id를식별자로?
    @Override
    public String getName() {
        return member.getEmail();
    }
}
