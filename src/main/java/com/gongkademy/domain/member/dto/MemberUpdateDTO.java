package com.gongkademy.domain.member.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateDTO {
    private String email;
    private String newNickname;
    private String university;
    private String major;
    private String minor;

}
