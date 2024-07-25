package com.gongkademy.domain.member.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.gongkademy.domain.member.entity.Member;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    	return member.getMemberRoleList().stream()
                .map(role -> new SimpleGrantedAuthority(role.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getEmail() {
        return member.getEmail();
    }

    public String getNickname() {
        return member.getNickname();
    }

    public Boolean getAgreeMarketing(){
        return member.getAgreeMarketing();
    }

    @Override
    public String getUsername() {
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

    public LocalDate getBirthday() {
        return member.getBirthday();
    }

    public List<String> getRoleNames() {
        return Arrays.stream(member.getMemberRoleList().toArray()).map(Object::toString).toList();
    }


    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", getEmail());
        claims.put("nickname", getNickname());
        return claims;
    }

    @Override
    public String getName() {
        return member.getName();
    }

    public long getMemberId() { return member.getId(); }

    public boolean getIsNotificationEnabled() { return member.isNotificationEnabled(); }
}
