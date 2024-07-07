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
    private Boolean agreeMarketing;

    // agreeService, agreePrivacy는 로직 검사용, DB 저장 X
    private Boolean agreeService;
    private Boolean agreePrivacy;

}
