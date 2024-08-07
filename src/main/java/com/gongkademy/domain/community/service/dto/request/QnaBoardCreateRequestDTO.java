package com.gongkademy.domain.community.service.dto.request;


import com.gongkademy.domain.community.common.entity.board.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QnaBoardCreateRequestDTO {

    private BoardType boardType;
    private Long memberId;
    private String title;
    private String content;

    private Long courseId;
    private Long lectureId;
}
