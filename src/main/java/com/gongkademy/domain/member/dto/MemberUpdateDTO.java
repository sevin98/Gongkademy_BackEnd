package com.gongkademy.domain.member.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateDTO {
    private String email;
    private String newNickname;
    private Boolean agreeMarketing;
    private MultipartFile profileImage;
}
