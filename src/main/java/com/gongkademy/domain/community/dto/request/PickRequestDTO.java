package com.gongkademy.domain.community.dto.request;

import com.gongkademy.domain.board.entity.pick.PickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PickRequestDTO {

    private Long articleId;
    private Long memberId;
    private PickType pickType;

}
