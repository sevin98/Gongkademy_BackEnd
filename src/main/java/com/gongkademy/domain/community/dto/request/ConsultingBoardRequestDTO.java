package com.gongkademy.domain.community.dto.request;

import com.gongkademy.domain.community.entity.board.BoardType;
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
