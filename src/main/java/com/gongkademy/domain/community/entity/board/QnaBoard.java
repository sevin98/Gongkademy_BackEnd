package com.gongkademy.domain.community.entity.board;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class QnaBoard extends ImageBoard {

    private String lectureTitle;

    private String courseTitle;
}
