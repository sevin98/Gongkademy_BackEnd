package com.gongkademy.domain.member.dto;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class MemberDTO extends User {
    //Spring Security에서 쓰는 DTO는 user를 상속받거나 UserDetail~의 구현체여야함
    private String email;
    private String nickname;
    private String password;
    private String birthday;
    private List<String> roleNames=new ArrayList<>();

    public MemberDTO(String email,String password,String nickname,String birthday,List<String> roleNames){
        //우리는 프론트가 편하게 String으로 권한명을 넘길건데, 조상인 애는 객체를 받아야하기때문에 SimpleGrantedAuthority로 변화시켜줌
        super(email,password,roleNames.stream().map(str-> new SimpleGrantedAuthority("ROLE_"+str)).collect(Collectors.toList()));
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.roleNames = roleNames;
    }

    //jwt 문자열을 만들 때 데이터가 필요.즉 dto데이터를 이용해 claim을 만드는 기능
    //claim은 사용자의 property가 key-value로 담긴 jwt의 내용 (즉 payload의 일부)
    public Map<String,Object> getClaims(){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("email",email);
        dataMap.put("nickname",nickname);
//        dataMap.put("birthday",birthday);
//        dataMap.put("roleNames",roleNames);
        return dataMap;
    }

}
