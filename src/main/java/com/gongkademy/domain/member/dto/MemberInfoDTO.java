package com.gongkademy.domain.member.dto;

import lombok.*;
import org.joda.time.DateTime;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDTO {

    //멤버 정보 조회
    private String email;
    private String name;
    private String nickname;
    private String birthday;
    private String university;
    private String major;
    private String minor;
    private String member_role;
    private DateTime create_date;
}
