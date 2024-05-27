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
    private String nickname;
    private String email;
    private String password;
    private String birthday;
}
