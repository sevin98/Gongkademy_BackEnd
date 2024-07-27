package com.gongkademy.domain.community.service.dto.request;

import com.gongkademy.domain.community.common.entity.board.BoardType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultingBoardRequestDTO {
    private BoardType boardType;
    private Long memberId;
    private String title;
    private String content;
}
