package com.gongkademy.domain.community.dto.request;

import com.gongkademy.domain.board.entity.board.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDTO {

    private BoardType boardType;
    private Long memberId;
    private String title;
    private String content;
}
