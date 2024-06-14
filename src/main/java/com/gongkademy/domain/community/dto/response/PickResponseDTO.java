package com.gongkademy.domain.community.dto.response;

import com.gongkademy.domain.board.entity.pick.PickType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PickResponseDTO {

    private Long id;
    private Long articleId;
    private Long memberId;
    private PickType pickType;

}
