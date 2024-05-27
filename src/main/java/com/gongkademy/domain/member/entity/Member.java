package com.gongkademy.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "memberRoleList") //ElementCollection으로 잡힌 애들은 toString 제외를 해줘야 Lazy 로딩이 안됨
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String email;
    @NotEmpty
    private String nickname;
    @NotEmpty
    private String password;

    private LocalDate birthday;

    public void updateNickname(String nickname){
            this.nickname = nickname;
    }
    public void updatePassword(String password){
        this.password = password;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    public void addRole(MemberRole memberRole){
        memberRoleList.add(memberRole);
    }

    public void clearRole(){
        memberRoleList.clear();
    }

}