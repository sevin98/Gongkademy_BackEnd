package com.gongkademy.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSignUpDTO {
    private String email;
    private String name;
    private String nickname;
    private String birthday;
    private String university;
    private String major;
    private String minor;
}
