package com.gongkademy.domain.community.service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QnaBoardUpdateRequestDTO {
    private String title;
    private String content;
}
