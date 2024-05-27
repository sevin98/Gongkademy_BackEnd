package com.gongkademy.domain.member.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDTO {
    private String email;
    private String nickname;
    private String birthday;
}
